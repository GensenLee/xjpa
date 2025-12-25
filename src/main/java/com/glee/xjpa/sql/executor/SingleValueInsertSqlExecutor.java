package com.glee.xjpa.sql.executor;

import cn.hutool.core.bean.BeanUtil;
import com.glee.xjpa.constant.XjpaConstant;
import com.glee.xjpa.exception.XjpaExecuteException;
import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.sql.executor.session.ExecuteSession;
import com.glee.xjpa.table.EntityTable;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.util.EntityInsertUtil;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.executor.query.InsertQueryRequest;
import com.glee.xjpa.sql.executor.result.reader.Result;
import com.glee.xjpa.sql.logger.SqlLogger;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 单个值语句插入
 */
public class SingleValueInsertSqlExecutor<K, V> extends AbstractSqlExecutor<K, V> {

    public SingleValueInsertSqlExecutor(ExecuteSession executeSession, SqlLogger sqlLogger) {
        super(executeSession, sqlLogger);
    }

    @Override
    public Result execute(AbstractQueryRequest<K, V> query) throws SQLException {

        InsertQueryRequest<K, V> insertQueryRequest = (InsertQueryRequest<K, V>) query;
        Collection<V> entityValues = insertQueryRequest.getEntityValues();
        if (CollectionUtils.isEmpty(entityValues)) {
            throw new XjpaExecuteException("insert list should not be empty");
        }

        RepositoryContext<K, V> context = insertQueryRequest.getContext();
        Consumer<ResultSet> generatedKeysOrGetKeysConsumer = EntityInsertUtil.generatedKeysOrGetKeysConsumer(context);

        List<InsertProcessSql> processSqlList = prepareSql(query.getEntityTable(), entityValues);

        if (processSqlList.size() > 1) {
            // 批量操作是要求开启事务
            executeSession.requireTransactionEnabled();
        }

        int affectRow = 0;
        for (InsertProcessSql insertProcessSql : processSqlList) {
            affectRow += doExecuteUpdate(insertProcessSql.getFinalSql(), insertProcessSql.getFinalSqlParameters(),
                    generatedKeysOrGetKeysConsumer);
        }

        return Result.Builder.build(affectRow);
    }




    /**
     * @param entityTable
     * @param entityValues
     * @return
     */
    protected List<InsertProcessSql> prepareSql(EntityTable<K, V> entityTable, Collection<V> entityValues) {

        List<EntityTableField> entityTableFieldList = entityTable.getEntityTableFieldList();

        List<InsertProcessSql> result = new ArrayList<>();
        for (V entityValue : entityValues) {
            InsertProcessSql.ProcessSqlBuilder processSqlBuilder = InsertProcessSql.builder();

            //<editor-fold desc="set value">
            Map<Integer, Object> setValues = new HashMap<>();
            processSqlBuilder.withSetValueParameters(setValues);

            String setValueColumnString = concatSetValueColumns(entityTableFieldList, entityValue, setValues);
            if (setValueColumnString == null || setValues.isEmpty()) {
                // 没有需要更新的值
                continue;
            }
            processSqlBuilder.withSetValueString(setValueColumnString);
            //</editor-fold>


            String finalSqlStringBuilder = "insert into `" +
                    entityTable.getTableName() +
                    "` " +
                    setValueColumnString;

            processSqlBuilder.withFinalSql(finalSqlStringBuilder);
            InsertProcessSql processSql = processSqlBuilder.build();

            result.add(processSql);
        }

        return result;
    }



    /**
     * @param entityTableFieldList
     * @param entity
     * @param setValues
     * @return
     */
    private String concatSetValueColumns(List<EntityTableField> entityTableFieldList, V entity, Map<Integer, Object> setValues) {

        int index = 1;
        List<String> setColumnList = new ArrayList<>();

        for (EntityTableField entityTableField : entityTableFieldList) {


            if (isNullPrimaryKey(entity, entityTableField)) continue;

            Object value = BeanUtil.getFieldValue(entity, entityTableField.getJavaField().getName());
            if (value != null) {
                setColumnList.add(entityTableField.getTableFieldMetadata().getField());
                setValues.put(index++, value);
            }
        }

        if (setColumnList.isEmpty()) {
            return null;
        }
        String columnString = setColumnList.stream()
                .map(column -> "`" + column + "`")
                .collect(Collectors.joining(XjpaConstant.COMMA_MARK));

        String valueString = setColumnList.stream()
                .map(s -> XjpaConstant.QUESTION_MARK)
                .collect(Collectors.joining(XjpaConstant.COMMA_MARK));

        return "(" + columnString + ") values (" + valueString + ")";
    }

}
