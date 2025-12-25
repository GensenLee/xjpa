package com.glee.xjpa.sql.executor.command;

import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.sql.executor.AbstractSqlExecutor;
import com.glee.xjpa.sql.executor.query.SelectQueryRequest;
import com.glee.xjpa.sql.executor.result.reader.Result;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.result.parser.ResultParser;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description 单列查询执行
 */
@SuppressWarnings({"rawtypes"})
public class SingleColumnQueryExecuteRequestCommandAcceptor<K, V> implements QueryExecuteRequestCommandAcceptor {

    private final QueryExecuteRequestCommandAcceptor queryExecuteRequestCommandAcceptor;

    private final RepositoryContext<K, V> context;

    public SingleColumnQueryExecuteRequestCommandAcceptor(RepositoryContext<K, V> context) {
        this.context = context;
        this.queryExecuteRequestCommandAcceptor = new DefaultQueryExecuteRequestCommandAcceptor<>(context);
    }


    /**
     * @param queryRequest
     * @return
     */
    @Override
    public <E extends AbstractSqlExecutor> Result executeAndRead(AbstractQueryRequest queryRequest, Class<E> executorType) {
        return queryExecuteRequestCommandAcceptor.executeAndRead(queryRequest, executorType);
    }


    /**
     * @param queryRequest
     * @param executorType
     * @param resultType
     * @param <E>
     * @param <R>
     * @return
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public <E extends AbstractSqlExecutor, R> List<R> executeAndConvert(AbstractQueryRequest queryRequest, Class<E> executorType, Class<R> resultType) {
        Result result = executeAndRead(queryRequest, executorType);
        if (result == null || result.getAffectRow() <= 0) {
            return Collections.emptyList();
        }


        Collection<String> includeColumns = ((SelectQueryRequest<K, V>) queryRequest).includeColumns();
        String column = includeColumns.iterator().next();

        ResultParser resultParser = context.getResultParser();
        return resultParser.parseSingleColumn(result.getRawResults(), column, resultType);
    }


    /**
     * @param queryRequest
     * @param executorType
     * @param <E>
     * @return
     */
    @Override
    public <E extends AbstractSqlExecutor> int executeAndGetAffect(AbstractQueryRequest queryRequest, Class<E> executorType) {
        return queryExecuteRequestCommandAcceptor.executeAndGetAffect(queryRequest, executorType);
    }

}
