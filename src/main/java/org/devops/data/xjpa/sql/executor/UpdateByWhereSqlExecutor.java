package org.devops.data.xjpa.sql.executor;

import lombok.extern.slf4j.Slf4j;
import org.devops.core.utils.constant.CommonConstant;
import org.devops.data.xjpa.exception.XjpaNoWhereException;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.query.UpdateByWhereQueryRequest;
import org.devops.data.xjpa.sql.executor.result.reader.Result;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.sql.where.handler.IQueryWhereHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 根据where条件更新
 */
@Slf4j
public class UpdateByWhereSqlExecutor<K, V> extends AbstractSqlExecutor<K, V> {

    public UpdateByWhereSqlExecutor(ExecuteSession executeSession, SqlLogger sqlLogger) {
        super(executeSession, sqlLogger);
    }


    @Override
    public Result execute(AbstractQueryRequest<K, V> query) throws SQLException {
        UpdateByWhereQueryRequest<K, V> updateModelQueryRequest = (UpdateByWhereQueryRequest<K, V>) query;
        if (query.emptyWhere()) {
            throw new XjpaNoWhereException("update where required");
        }

//        executeSession.requireTransactionEnabled();

        UpdateProcessSql processSql = prepareSql(query.getEntityTable(),
                updateModelQueryRequest.getQueryWhereHandler(),
                updateModelQueryRequest.getLimitHandler(),
                updateModelQueryRequest.getSortHandler(),
                updateModelQueryRequest.getUpdateValueHandler());

        int affectRow = doExecute(processSql);

        return Result.Builder.build(affectRow);
    }

    /**
     * @param processSql
     * @return
     */
    private int doExecute(UpdateProcessSql processSql) throws SQLException {
        return doExecuteUpdate(processSql.getFinalSql(), processSql.getFinalSqlParameters());
    }

    /**
     * @param entityTable
     * @param queryWhereHandler
     * @param limitHandler
     * @param sortHandler
     * @param updateValueHandler
     * @return
     */
    protected UpdateProcessSql prepareSql(EntityTable<K, V> entityTable,
                                          IQueryWhereHandler queryWhereHandler,
                                          LimitHandler limitHandler,
                                          SortHandler sortHandler,
                                          UpdateValueHandler updateValueHandler) {

        UpdateProcessSql.ProcessSqlBuilder processSqlBuilder = UpdateProcessSql.builder();

        Map<Integer, Object> setValues = new HashMap<>();
        String setValueColumnString = concatSetValueColumns(updateValueHandler, setValues);
        processSqlBuilder.withSetValueString(setValueColumnString)
                .withSetValueParameters(setValues);

        String whereString = queryWhereHandler.toWhereString();
        Map<Integer, Object> whereValues = queryWhereHandler.whereValues();
        processSqlBuilder.withWhereString(whereString)
                .withWhereParameters(whereValues);

        String finalSql = "update `" +
                entityTable.getTableName() +
                "` set " +
                setValueColumnString +
                " where " +
                whereString +
                " " +
                concatSortString(sortHandler) +
                " " +
                concatLimitString(limitHandler);

        return processSqlBuilder
                .withFinalSql(finalSql)
                .build();
    }


    /**
     * @param updateValueHandler
     * @param setValues
     * @return
     */
    private String concatSetValueColumns(UpdateValueHandler updateValueHandler, Map<Integer, Object> setValues) {

        List<String> setColumnList = new ArrayList<>();

        Map<Integer, Object> updateValues = updateValueHandler.updateValues();

        List<String> columnList = updateValueHandler.updateColumnList();
        for (int i = 0; i < columnList.size(); i++) {
            String column = columnList.get(i);
            Object value = updateValues.get(i + 1);
            if (value != null) {
                setColumnList.add(column);
                setValues.put(i + 1, value);
            }
        }

        if (setColumnList.isEmpty()) {
            return null;
        }

        return setColumnList.stream()
                .map(updateValueHandler::defineSetPhrase)
                .collect(Collectors.joining(CommonConstant.COMMA_MARK));
    }

}
