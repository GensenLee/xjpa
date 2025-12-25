package com.glee.xjpa.sql.executor;

import cn.hutool.core.bean.BeanUtil;
import com.glee.xjpa.constant.XjpaConstant;
import com.glee.xjpa.exception.XjpaExecuteException;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.executor.query.InsertQueryRequest;
import com.glee.xjpa.sql.executor.result.reader.Result;
import com.glee.xjpa.sql.executor.session.ExecuteSession;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.table.EntityTable;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.util.PreparedStatementUtil;
import com.glee.xjpa.util.PstParameter;
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
        long start = System.currentTimeMillis();
        int affectRow = preparedStatement.executeUpdate();
        long end = System.currentTimeMillis();
        sqlLogger.logAffect(affectRow, start, end);
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
                .collect(Collectors.joining(XjpaConstant.COMMA_MARK));
        String insertValuesPlaceholderString = "(" + insertColumns + ") values ";

        String valuePlaceholder = "(" + entityTableFieldList.stream()
                .map(entityTableField -> "?")
                .collect(Collectors.joining(XjpaConstant.COMMA_MARK)) + ")";

        insertValuesPlaceholderString += entityValues.stream()
                .map(e -> valuePlaceholder)
                .collect(Collectors.joining(XjpaConstant.COMMA_MARK));


        InsertProcessSql.ProcessSqlBuilder processSqlBuilder = InsertProcessSql.builder();
        processSqlBuilder.withSetValueString(insertValuesPlaceholderString);

        Map<Integer, Object> insertValues = new HashMap<>();

        processSqlBuilder.withSetValueParameters(insertValues);

        AtomicInteger index = new AtomicInteger(1);
        for (V entityValue : entityValues) {
            Map<Integer, PstParameter> entityInsertValues = entityTableFieldList.stream()
                    .collect(Collectors.toMap(entityTableField -> index.getAndIncrement(),
                            entityTableField ->
                                    new PstParameter(BeanUtil.getFieldValue(entityValue,
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
