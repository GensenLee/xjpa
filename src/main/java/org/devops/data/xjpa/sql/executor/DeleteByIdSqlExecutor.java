package org.devops.data.xjpa.sql.executor;

import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.query.DeleteByIdQueryRequest;
import org.devops.data.xjpa.sql.executor.result.reader.Result;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.table.EntityTableField;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 根据传入id删除
 */
public class DeleteByIdSqlExecutor<K, V> extends AbstractSqlExecutor<K, V> {
    public DeleteByIdSqlExecutor(ExecuteSession executeSession, SqlLogger sqlLogger) {
        super(executeSession, sqlLogger);
    }


    @Override
    public Result execute(AbstractQueryRequest<K, V> query) throws SQLException {
//        executeSession.requireTransactionEnabled();

        DeleteByIdQueryRequest<K, V> deleteByIdQueryRequest = (DeleteByIdQueryRequest<K, V>) query;

        List<DeleteProcessSql> processSqlList = prepareSql(deleteByIdQueryRequest.getEntityTable(), deleteByIdQueryRequest.getKeys());

        int affectRow = doExecute(processSqlList);

        return Result.Builder.build(affectRow);
    }

    /**
     * @param processSqlList
     * @return
     */
    private int doExecute(List<DeleteProcessSql> processSqlList) throws SQLException {

        return executeBatchUpdate(processSqlList.get(0).getFinalSql(), processSqlList);
    }


    /**
     * @param entityTable
     * @param keys
     * @return
     */
    protected List<DeleteProcessSql> prepareSql(EntityTable<K, V> entityTable, List<K> keys) {
        EntityTableField entityTableField = entityTable.getPrimaryKeyField();
        if (entityTableField.getJavaField() == null) {
            throw new XjpaExecuteException("A primary key field required");
        }

        String whereString = entityTableField.getTableFieldMetadata().getField() + " = ?";
        String finalSql = "delete from `" +
                entityTable.getTableName() +
                "` " +
                "where " +
                whereString;
        List<DeleteProcessSql> result = new ArrayList<>();
        for (K key : keys) {
            result.add(DeleteProcessSql.builder()
                    .withFinalSql(finalSql)
                    .withWhereString(whereString)
                    .withWhereParameters(Collections.singletonMap(1, key))
                    .build());
        }

        return result;
    }
}
