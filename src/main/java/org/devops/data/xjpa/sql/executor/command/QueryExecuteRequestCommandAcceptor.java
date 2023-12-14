package org.devops.data.xjpa.sql.executor.command;

import org.devops.data.xjpa.sql.executor.AbstractSqlExecutor;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.result.reader.Result;

import java.util.List;

/**
 * query execute request 统一接收
 * @author GENSEN
 */
@SuppressWarnings({"rawtypes"})
public interface QueryExecuteRequestCommandAcceptor {

    /**
     * @param queryRequest
     * @param executorType
     * @param <E>
     * @return
     */
    <E extends AbstractSqlExecutor> Result executeAndRead(AbstractQueryRequest queryRequest, Class<E> executorType);

    /**
     * @param queryRequest
     * @param executorType
     * @param resultType
     * @param <E>
     * @param <R>
     * @return
     */
    <E extends AbstractSqlExecutor, R> List<R> executeAndConvert(AbstractQueryRequest queryRequest, Class<E> executorType, Class<R> resultType);

    /**
     * @param queryRequest
     * @param executorType
     * @param <E>
     * @return
     */
    <E extends AbstractSqlExecutor> int executeAndGetAffect(AbstractQueryRequest queryRequest, Class<E> executorType);

}
