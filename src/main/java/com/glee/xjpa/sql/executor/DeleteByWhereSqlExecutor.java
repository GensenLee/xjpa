package com.glee.xjpa.sql.executor;

import com.glee.xjpa.exception.XjpaNoWhereException;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.executor.query.DeleteByWhereQueryRequest;
import com.glee.xjpa.sql.executor.result.reader.Result;
import com.glee.xjpa.sql.executor.session.ExecuteSession;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.sql.where.handler.IQueryWhereHandler;
import com.glee.xjpa.table.EntityTable;

import java.sql.SQLException;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 使用where删除
 */
public class DeleteByWhereSqlExecutor<K, V> extends AbstractSqlExecutor<K, V> {

    public DeleteByWhereSqlExecutor(ExecuteSession executeSession, SqlLogger sqlLogger) {
        super(executeSession, sqlLogger);
    }


    @Override
    public Result execute(AbstractQueryRequest<K, V> query) throws SQLException {
//        executeSession.requireTransactionEnabled();

        DeleteByWhereQueryRequest<K, V> deleteModelQueryRequest = (DeleteByWhereQueryRequest<K, V>) query;
        if (query.emptyWhere()) {
            throw new XjpaNoWhereException("delete where required");
        }

        DeleteProcessSql processSql = prepareSql(query.getEntityTable(),
                deleteModelQueryRequest.getQueryWhereHandler(),
                deleteModelQueryRequest.getLimitHandler(),
                deleteModelQueryRequest.getSortHandler());

        int affectRow = doExecuteUpdate(processSql.getFinalSql(), processSql.getFinalSqlParameters());

        return Result.Builder.build(affectRow);
    }



    protected DeleteProcessSql prepareSql(EntityTable<K, V> entityTable, IQueryWhereHandler queryWhereHandler,
                                          LimitHandler limitHandler, SortHandler sortHandler) {
        String formatWhereString = queryWhereHandler.toWhereString();
        String finalSql = "delete from `" +
                entityTable.getTableName() +
                "` " +
                "where " +
                formatWhereString +
                " " +
                concatSortString(sortHandler) +
                " " +
                concatLimitString(limitHandler);

        return DeleteProcessSql.builder()
                .withFinalSql(finalSql)
                .withWhereString(formatWhereString)
                .withWhereParameters(queryWhereHandler.whereValues())
                .build();
    }
}
