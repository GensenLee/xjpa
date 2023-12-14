package org.devops.data.xjpa.sql.executor;

import lombok.extern.slf4j.Slf4j;
import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.query.InsertQueryRequest;
import org.devops.data.xjpa.sql.executor.result.reader.Result;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.table.EntityTableField;
import org.devops.data.xjpa.util.PreparedStatementUtil;
import org.devops.core.utils.constant.CommonConstant;
import org.devops.core.utils.util.BeanUtil;
import org.devops.data.xjpa.util.PstParameter;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 多值语句插入，速度优于 SingleValueInsertSqlExecutor
 */
@Slf4j
@Deprecated
public class MultipleValueInsertSqlExecutor<K, V> extends AbstractSqlExecutor<K, V> {

    public MultipleValueInsertSqlExecutor(ExecuteSession executeSession, SqlLogger sqlLogger) {
        super(executeSession, sqlLogger);
    }

    @Override
    public Result execute(AbstractQueryRequest<K, V> query) throws SQLException {
        InsertQueryRequest<K, V> insertQueryRequest = (InsertQueryRequest<K, V>) query;
        List<V> entityValues = insertQueryRequest.getEntityValues();
        if (CollectionUtils.isEmpty(entityValues)) {
            throw new XjpaExecuteException("empty insert list");
        }

        InsertProcessSql prepareSql = prepareSql(query.getEntityTable(), entityValues);

        int affectRow = doExecute(prepareSql);

        return Result.Builder.build(affectRow);
    }


    /**
     * @param processSql
     * @return
     * @throws SQLException
     */
    private int doExecute(InsertProcessSql processSql) throws SQLException {
        sqlLogger.logSql(processSql.getStatementSql(), processSql.getStatementParameters());
        PreparedStatement preparedStatement = executeSession.updateStatement(processSql.getStatementSql());
        PreparedStatementUtil.setParameters(preparedStatement, processSql.getStatementParameters());
        int affectRow = preparedStatement.executeUpdate();
        sqlLogger.logAffect(affectRow);
        return affectRow;
    }



    /**
     * @param entityTable
     * @param entityValues
     * @return
     */
    protected InsertProcessSql prepareSql(EntityTable<K, V> entityTable, List<V> entityValues) {

        List<EntityTableField> entityTableFieldList = entityTable.getEntityTableFieldList();

        String insertColumns = entityTableFieldList.stream()
                .map(entityTableField -> "`" + entityTableField.getTableFieldMetadata().getField() + "`")
                .collect(Collectors.joining(CommonConstant.COMMA_MARK));
        String insertValuesPlaceholderString = "(" + insertColumns + ") values ";

        String valuePlaceholder = "(" + entityTableFieldList.stream()
                .map(entityTableField -> "?")
                .collect(Collectors.joining(CommonConstant.COMMA_MARK)) + ")";

        insertValuesPlaceholderString += entityValues.stream()
                .map(e -> valuePlaceholder)
                .collect(Collectors.joining(CommonConstant.COMMA_MARK));


        InsertProcessSql.ProcessSqlBuilder processSqlBuilder = InsertProcessSql.builder();
        processSqlBuilder.withSetValueString(insertValuesPlaceholderString);

        Map<Integer, Object> insertValues = new HashMap<>();

        processSqlBuilder.withSetValueParameters(insertValues);

        AtomicInteger index = new AtomicInteger(1);
        for (V entityValue : entityValues) {
            Map<Integer, PstParameter> entityInsertValues = entityTableFieldList.stream()
                    .collect(Collectors.toMap(entityTableField -> index.getAndIncrement(),
                            entityTableField ->
                                    new PstParameter(BeanUtil.getValue(entityValue,
                                            entityTableField.getJavaField().getName()), entityTableField)));
            insertValues.putAll(entityInsertValues);
        }

        String finalSql = "insert into `" +
                entityTable.getTableName() +
                "` " +
                insertValuesPlaceholderString;

        processSqlBuilder.withFinalSql(finalSql);
        return processSqlBuilder.build();
    }
    
}
