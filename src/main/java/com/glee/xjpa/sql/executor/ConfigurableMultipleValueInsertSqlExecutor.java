package com.glee.xjpa.sql.executor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.constant.XjpaConstant;
import com.glee.xjpa.exception.XjpaExecuteException;
import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.executor.query.InsertQueryRequest;
import com.glee.xjpa.sql.executor.result.reader.Result;
import com.glee.xjpa.sql.executor.session.ExecuteSession;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.table.EntityTable;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.util.EntityInsertUtil;
import com.glee.xjpa.util.PstParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 多值语句插入，速度优于 SingleValueInsertSqlExecutor，可配置单条sql value 最大长度
 */
public class ConfigurableMultipleValueInsertSqlExecutor<K, V> extends AbstractSqlExecutor<K, V> {

    protected static final Logger logger = LoggerFactory.getLogger(ConfigurableMultipleValueInsertSqlExecutor.class);

    public static final String insertValueLengthLimitName = "devops.data.xjpa.insertValueLengthLimit";

    public static final String ignoreGenerateKeyName = "devops.data.xjpa.ignoreGenerateKey";


    /**
     * 单条sql语句value长度上限，默认 200
     */
    private int insertValueLengthLimit = 200;

    /**
     * 是否跳过读取数据库自增主键，默认 false
     */
    private boolean ignoreGenerateKey = false;

    public ConfigurableMultipleValueInsertSqlExecutor(ExecuteSession executeSession, SqlLogger sqlLogger) {
        super(executeSession, sqlLogger);
    }

    @Override
    public Result execute(AbstractQueryRequest<K, V> query) throws SQLException {

        InsertQueryRequest<K, V> insertQueryRequest = (InsertQueryRequest<K, V>) query;
        List<V> entityValues = insertQueryRequest.getEntityValues();
        if (CollectionUtils.isEmpty(entityValues)) {
            throw new XjpaExecuteException("empty insert list");
        }

        if (entityValues.size() > 1) {
            // 批量操作时要求开启事务
            executeSession.requireTransactionEnabled();
        }

        RepositoryContext<K, V> context = insertQueryRequest.getContext();

        Consumer<ResultSet> keysConsumer = EntityInsertUtil.generatedKeysOrGetKeysConsumer(context);

        int insertValueLengthLimit = getInsertValueLengthLimit(insertQueryRequest.getContext());

        List<List<V>> partitions = ListUtil.partition(entityValues, insertValueLengthLimit);


        // 这里需要使用队列维持 entityValues 列表顺序
        Queue<InsertSet> insertSetQueue = new LinkedList<>();

        for (List<V> partition : partitions) {
            InsertProcessSql insertProcessSql = prepareSql(insertQueryRequest.getEntityTable(), partition);

            Optional<InsertSet> insertSetOptional = insertSetQueue.stream()
                    .filter(insertSet -> insertSet.finalSql.equals(insertProcessSql.getFinalSql()))
                    .findFirst();

            InsertSet insertSetInQueue = insertSetOptional.orElseGet(() -> {
                InsertSet insertSet = new InsertSet(insertProcessSql.getFinalSql());
                insertSetQueue.add(insertSet);
                return insertSet;
            });

            insertSetInQueue.addProcessSql(insertProcessSql);
        }

        Consumer<ResultSet> acceptGeneratedKeys = getIgnoreGenerateKey(context) ? null : keysConsumer;

        int affectRow = doExecute(insertSetQueue, acceptGeneratedKeys);
        return Result.Builder.build(affectRow);

    }


    /**
     * @param context
     * @return
     */
    private int getInsertValueLengthLimit(RepositoryContext<K, V> context) {
        Object attribute = context.getAttribute(insertValueLengthLimitName);
        if (attribute == null) {
            return insertValueLengthLimit;
        }
        int anInt = NumberUtil.parseInt(String.valueOf(attribute));
        if (anInt <= 0) {
            logger.error("invalid property value [{}] for [{}]", attribute, insertValueLengthLimitName);
            return insertValueLengthLimit;
        } else {
            insertValueLengthLimit = anInt;
        }
        return insertValueLengthLimit;
    }

    /**
     * @param context
     * @return
     */
    private boolean getIgnoreGenerateKey(RepositoryContext<K, V> context) {
        Object attribute = context.getAttribute(ignoreGenerateKeyName);
        if (StrUtil.isEmpty((CharSequence) attribute)) {
            return ignoreGenerateKey;
        }
        return ignoreGenerateKey = Boolean.parseBoolean(String.valueOf(attribute));
    }

    /**
     * @param sqlBatch
     * @return
     * @throws SQLException
     */
    private int doExecute(Queue<InsertSet> sqlBatch, Consumer<ResultSet> generatedKeysConsumer) throws SQLException {
        if (sqlBatch.isEmpty()) {
            return 0;
        }
        int affectRow = 0;

        while (!sqlBatch.isEmpty()) {
            InsertSet insertSet = sqlBatch.remove();

            if (insertSet.processSqlList.stream()
                    .anyMatch(processSql -> processSql.getFinalSqlParameters().isEmpty())) {
                // 没有条件值
                continue;
            }

            ensureSqlParameterSameLength(insertSet.processSqlList);

            int batchUpdatedRow = executeBatchUpdate(insertSet.finalSql, insertSet.processSqlList, generatedKeysConsumer);

            affectRow += batchUpdatedRow;

        }
        return affectRow;
    }


    /**
     * @param entityTable
     * @param entityValues
     * @return
     */
    protected InsertProcessSql prepareSql(EntityTable<K, V> entityTable, List<V> entityValues) {


        final StringBuilder finalSqlStringBuilder = new StringBuilder();

        InsertProcessSql.ProcessSqlBuilder processSqlBuilder = InsertProcessSql.builder();

        Map<Integer, Object> insertValues = new HashMap<>();

        processSqlBuilder.withSetValueParameters(insertValues);


        int index = 1;
        for (V entityValue : entityValues) {
            List<EntityTableField> entityTableFieldList = filterNullFields(entityTable, entityValue);

            if (entityTableFieldList.isEmpty()) {
                continue;
            }


            finalSqlStringBuilder.append("insert into `").append(entityTable.getTableName()).append("` ");


            finalSqlStringBuilder.append("(")
                    .append(entityTableFieldList.stream()
                            .map(entityTableField -> "`" + entityTableField.getTableFieldMetadata().getField() + "`")
                            .collect(Collectors.joining(XjpaConstant.COMMA_MARK)))
                    .append(") values (")
                    .append(entityTableFieldList.stream()
                            .map(entityTableField -> "?")
                            .collect(Collectors.joining(XjpaConstant.COMMA_MARK)))
                    .append(");");


            Map<Integer, Object> entityInsertValues = new HashMap<>();
            for (EntityTableField entityTableField : entityTableFieldList) {

                if (isNullPrimaryKey(entityValue, entityTableField)) {
                    // 主键不设置默认值
                    continue;
                }

                Object value = BeanUtil.getFieldValue(entityValue, entityTableField.getJavaField().getName());

                entityInsertValues.put(index++, new PstParameter(value, entityTableField));
//                // 批量插入时全部实体记录都通过同一条sql插入，所以要保持字段长度一致，null需要替换成默认值
//                if (value == null) {
//                    value = entityTableField.getTableFieldMetadata().getDefault();
//                }

            }
//            这种方式不能存在null值
//            Map<Integer, Object> entityInsertValues = entityTableFieldList.stream()
//                    .collect(Collectors.toMap(entityTableField -> index.getAndIncrement(),
//                            entityTableField -> BeanUtil.getValue(entityValue, entityTableField.getJavaField().getName()),
//                            (v1, v2) -> v1));
            insertValues.putAll(entityInsertValues);
        }


        processSqlBuilder.withFinalSql(finalSqlStringBuilder.toString());
        return processSqlBuilder.build();
    }

    /**
     * @param entityTable
     * @param entity
     * @return
     */
    private List<EntityTableField> filterNullFields(EntityTable<K, V> entityTable, V entity) {
        return entityTable.getEntityTableFieldList().stream()
                .filter(entityTableField -> BeanUtil.getFieldValue(entity, entityTableField.getJavaField().getName()) != null)
                .collect(Collectors.toList());
    }


    static class InsertSet {
        final String finalSql;
        final List<InsertProcessSql> processSqlList;

        public InsertSet(String finalSql) {
            this.finalSql = finalSql;
            this.processSqlList = new ArrayList<>();
        }

        void addProcessSql(InsertProcessSql processSql) {
            processSqlList.add(processSql);
        }
    }
}
