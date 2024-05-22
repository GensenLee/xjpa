package org.devops.data.xjpa.sql.executor;

import org.devops.data.xjpa.exception.XjpaNoWhereException;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.query.DeleteByWhereQueryRequest;
import org.devops.data.xjpa.sql.executor.result.reader.Result;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.sql.where.handler.IQueryWhereHandler;

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
