package org.devops.data.xjpa.sql.executor;

import cn.hutool.core.bean.BeanUtil;
import org.devops.data.xjpa.constant.XjpaConstant;
import org.devops.data.xjpa.exception.XjpaNoWhereException;
import org.devops.data.xjpa.repository.impl.enhance.EnhanceCurdBound;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.query.UpdateByEntityQueryRequest;
import org.devops.data.xjpa.sql.executor.result.reader.Result;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.table.EntityTableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 根据实体类更新
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class UpdateByEntitySqlExecutor<K, V> extends AbstractSqlExecutor<K, V> {

    protected static final Logger logger = LoggerFactory.getLogger(UpdateByEntitySqlExecutor.class);


    public UpdateByEntitySqlExecutor(ExecuteSession executeSession, SqlLogger sqlLogger) {
        super(executeSession, sqlLogger);
    }


    @Override
    public Result execute(AbstractQueryRequest<K, V> query) throws SQLException {

        UpdateByEntityQueryRequest<K, V> updateByEntityQueryRequest = (UpdateByEntityQueryRequest<K, V>) query;

        List<V> entityValues = updateByEntityQueryRequest.getEntityValues();
        if (CollectionUtils.isEmpty(entityValues)) {
            logger.error("无法update空的数据列表");
            throw new IllegalArgumentException("empty update list");
        }

        Collection<String> includeColumns = Collections.emptyList();
        EnhanceCurdBound enhanceCurdBound = updateByEntityQueryRequest.getContext().getSingleton(EnhanceCurdBound.class);
        if (enhanceCurdBound != null) {
            includeColumns = enhanceCurdBound.getIncludeColumns();
        }

        List<UpdateProcessSql> processSqlList = prepareSql(query.getEntityTable(), entityValues, includeColumns);

        Map<String, List<UpdateProcessSql>> sqlGroupCollect = processSqlList.stream()
                .collect(Collectors.groupingBy(UpdateProcessSql::getFinalSql));

        if (sqlGroupCollect.size() > 1) {
            executeSession.requireTransactionEnabled();
        }

        int affectRow = doExecute(sqlGroupCollect);

        return Result.Builder.build(affectRow);
    }

    /**
     * @param sqlBatch
     * @return
     */
    private int doExecute(Map<String, List<UpdateProcessSql>> sqlBatch) throws SQLException {
        if (sqlBatch.isEmpty()) {
            return 0;
        }

        int affectRow = 0;
        for (Map.Entry<String, List<UpdateProcessSql>> entry : sqlBatch.entrySet()) {
            if (entry.getValue().stream()
                    .anyMatch(processSql -> processSql.getFinalSqlParameters().isEmpty())) {
                // 没有条件值
                continue;
            }

            ensureSqlParameterSameLength(entry.getValue());

            int batchUpdatedRow = executeBatchUpdate(entry.getKey(), entry.getValue());

            affectRow += batchUpdatedRow;
        }

        return affectRow;
    }

    /**
     * @param entityTable
     * @param entityValues
     * @return
     */
    protected List<UpdateProcessSql> prepareSql(EntityTable<K, V> entityTable, List<V> entityValues, Collection<String> includeColumns) {

        Map<Field, String> javaFieldColumnMap = entityTable.getEntityTableFieldList()
                .stream()
                .filter(tf -> CollectionUtils.isEmpty(includeColumns) ||
                        (includeColumns.contains(tf.getTableFieldMetadata().getField()) || includeColumns.contains(tf.getJavaField().getName())))
                .collect(Collectors.toMap(EntityTableField::getJavaField,
                        entityTableField -> entityTableField.getTableFieldMetadata().getField()));


        EntityTableField primaryKeyField = entityTable.getPrimaryKeyField();
        // id 列不需要set
        javaFieldColumnMap.remove(primaryKeyField.getJavaField());

        List<UpdateProcessSql> result = new ArrayList<>();
        for (V entityValue : entityValues) {
            UpdateProcessSql.ProcessSqlBuilder processSqlBuilder = UpdateProcessSql.builder();

            //<editor-fold desc="set value">
            Map<Integer, Object> setValues = new HashMap<>();

            String setValueColumnString = concatSetValueColumns(javaFieldColumnMap, entityValue, setValues);
            if (setValueColumnString == null || setValues.isEmpty()) {
                // 没有需要更新的值
                continue;
            }
            processSqlBuilder.withSetValueString(setValueColumnString)
                    .withSetValueParameters(setValues);
            //</editor-fold>


            //<editor-fold desc="where 条件">
            String whereString = "`" + entityTable.getPrimaryKeyField().getColumn().name() + "` = ?";
            processSqlBuilder.withWhereString(whereString);
            K priValue = entityTable.getPrimaryKeyFieldValue(entityValue);
            if (priValue == null) {
                throw new XjpaNoWhereException("id required where update");
            }
            Map<Integer, Object> whereParameters = Collections.singletonMap(1, priValue);
            processSqlBuilder.withWhereParameters(whereParameters);
            //</editor-fold>


            String finalSql = "update `" +
                    entityTable.getTableName() +
                    "` set " +
                    setValueColumnString +
                    " where " +
                    whereString;

            processSqlBuilder.withFinalSql(finalSql);
            UpdateProcessSql processSql = processSqlBuilder.build();
            result.add(processSql);
        }

        return result;
    }

    /**
     * @param javaFieldColumnMap
     * @param entity
     * @param setValues
     * @return
     */
    private String concatSetValueColumns(Map<Field, String> javaFieldColumnMap, V entity, Map<Integer, Object> setValues) {

        int index = 1;
        List<String> setColumnList = new ArrayList<>();
        for (Map.Entry<Field, String> entry : javaFieldColumnMap.entrySet()) {
            Object value = BeanUtil.getFieldValue(entity, entry.getKey().getName());
            if (value != null) {
                setColumnList.add(entry.getValue());
                setValues.put(index++, value);
            }
        }
        if (setColumnList.isEmpty()) {
            return null;
        }

        return setColumnList.stream()
                .map(c -> "`" + c + "` = ?")
                .collect(Collectors.joining(XjpaConstant.COMMA_MARK));
    }
}
