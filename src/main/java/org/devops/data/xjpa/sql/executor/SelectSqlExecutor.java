package org.devops.data.xjpa.sql.executor;

import cn.hutool.core.util.StrUtil;
import org.devops.data.xjpa.constant.XjpaConstant;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.query.SelectQueryRequest;
import org.devops.data.xjpa.sql.executor.result.reader.Result;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.sql.where.handler.IQueryWhereHandler;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.util.PreparedStatementUtil;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description select
 */
public class SelectSqlExecutor<K, V> extends AbstractSqlExecutor<K, V> {


    public SelectSqlExecutor(ExecuteSession executeSession, SqlLogger sqlLogger) {
        super(executeSession, sqlLogger);
    }

    @Override
    public Result execute(AbstractQueryRequest<K, V> query) throws SQLException {
        SelectQueryRequest<K, V> queryRequest = (SelectQueryRequest<K, V>) query;

        SelectProcessSql processSql = prepareSql(query.getEntityTable(),
                queryRequest.getQueryWhereHandler(),
                queryRequest.getLimitHandler(),
                queryRequest.getSortHandler(),
                queryRequest.includeColumns(),
                queryRequest.isDistinct(),
                queryRequest.groupingColumns(),
                queryRequest.getHavingString());

        return doExecute(processSql);
    }


    /**
     * @param processSql
     * @return
     */
    private Result doExecute(SelectProcessSql processSql) throws SQLException {
        PreparedStatement preparedStatement = executeSession.readStatement(processSql.getFinalSql());
        sqlLogger.logSql(processSql.getFinalSql(), processSql.getFinalSqlParameters());
        PreparedStatementUtil.setParameters(preparedStatement, processSql.getFinalSqlParameters());
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        Result result = Result.Builder.build(resultSet);
        sqlLogger.logResult(resultSet);
        return result;
    }

    /**
     * @param entityTable
     * @param queryWhereHandler
     * @param limitHandler
     * @param sortHandler
     * @param includeColumns
     * @param distinct
     * @param groupingColumns
     * @param havingString
     * @return
     */
    protected SelectProcessSql prepareSql(EntityTable<K, V> entityTable,
                                          IQueryWhereHandler queryWhereHandler,
                                          LimitHandler limitHandler,
                                          SortHandler sortHandler,
                                          Collection<String> includeColumns,
                                          boolean distinct,
                                          Collection<String> groupingColumns,
                                          String havingString) {

        SelectProcessSql.ProcessSqlBuilder processSqlBuilder = SelectProcessSql.builder();

        StringBuilder finalSqlBuilder = new StringBuilder("select ");

        if (distinct) {
            finalSqlBuilder.append(" distinct ");
        }

        finalSqlBuilder.append(concatIncludeColumns(includeColumns, entityTable))
                .append(" from `")
                .append(entityTable.getTableName())
                .append("`");

        if (!queryWhereHandler.isEmpty()) {
            String whereString = queryWhereHandler.toWhereString();
            Map<Integer, Object> whereValues = queryWhereHandler.whereValues();
            processSqlBuilder.withWhereString(whereString)
                    .withWhereParameters(whereValues);
            finalSqlBuilder.append(" where ")
                    .append(whereString);
        }

        if (!CollectionUtils.isEmpty(groupingColumns)) {
            List<String> columns = entityTable.getEntityTableFieldList().stream()
                    .map(entityTableField -> entityTableField.getTableFieldMetadata().getField())
                    .collect(Collectors.toList());
            String groupingString = " group by " + groupingColumns.stream()
                    .map(column -> columns.contains(column) ? "`" + column + "`" : column)
                    .collect(Collectors.joining(XjpaConstant.COMMA_MARK));
            finalSqlBuilder.append(groupingString);

            if (StrUtil.isNotEmpty(havingString)) {
                finalSqlBuilder.append(" having ").append(havingString);
            }
        }

        finalSqlBuilder.append(concatSortString(sortHandler))
                .append(concatLimitString(limitHandler));

        return processSqlBuilder
                .withFinalSql(finalSqlBuilder.toString())
                .build();
    }

    /**
     * @param includeColumns
     * @param entityTable
     * @return
     */
    private String concatIncludeColumns(Collection<String> includeColumns, EntityTable<K, V> entityTable) {
        if (CollectionUtils.isEmpty(includeColumns)) {
            return XjpaConstant.ASTERISK_MARK;
        }
        List<String> tableFieldList = entityTable.getEntityTableFieldList().stream()
                .map(entityTableField -> entityTableField.getTableFieldMetadata().getField())
                .collect(Collectors.toList());
        return includeColumns.stream()
                .distinct()
                .map(c -> tableFieldList.contains(c) ? "`" + c + "`" : c)
                .collect(Collectors.joining(XjpaConstant.COMMA_MARK));
    }
}
