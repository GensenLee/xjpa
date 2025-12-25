package com.glee.xjpa.sql.executor.command;

import com.glee.xjpa.exception.XjpaExecuteException;
import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.sql.executor.AbstractSqlExecutor;
import com.glee.xjpa.sql.executor.result.reader.Result;
import com.glee.xjpa.sql.executor.session.ExecuteSession;
import com.glee.xjpa.util.SqlExecutorUtil;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.sql.result.parser.ResultParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description 默认执行器
 */
@SuppressWarnings({"rawtypes"})
public class DefaultQueryExecuteRequestCommandAcceptor<K, V> implements QueryExecuteRequestCommandAcceptor {
    protected static final Logger logger = LoggerFactory.getLogger(DefaultQueryExecuteRequestCommandAcceptor.class);

    private final RepositoryContext<K, V> context;

    public DefaultQueryExecuteRequestCommandAcceptor(RepositoryContext<K, V> context) {
        this.context = context;
    }


    /**
     * @param queryRequest
     * @return
     */
    @Override
    public <E extends AbstractSqlExecutor> Result executeAndRead(AbstractQueryRequest queryRequest, Class<E> executorType) {
        Constructor<E> constructor = null;
        try {
            constructor = executorType.getConstructor(ExecuteSession.class, SqlLogger.class);
        } catch (NoSuchMethodException e) {
            throw new XjpaExecuteException(e);
        }
        AbstractSqlExecutor executor = BeanUtils.instantiateClass(constructor, context.localSessionManager(), context.localSqlLogger());
        try {
            long start = System.currentTimeMillis();
            Result result = SqlExecutorUtil.execute(executor, queryRequest);
            logger.debug("[{}] executed with {} ms", executor.getClass().getSimpleName(), System.currentTimeMillis() - start);
            return result;
        } catch (SQLException e) {
            logger.error("execute error", e);
            throw new XjpaExecuteException(e);
        } finally {
            // 要往上层移动，否则delete、update这来需要前置检查的操作 会导致重复创建数据源连接 connection
            // context.dispose();
            // 2022-12-07 已经上移到 DisposeFacadeCreatedHandle.DisposeDelegateMethodInterceptor.intercept
        }
    }


    /**
     * @param queryRequest
     * @param executorType
     * @param resultType
     * @param <E>
     * @param <R>
     * @return
     */
    @Override
    public <E extends AbstractSqlExecutor, R> List<R> executeAndConvert(AbstractQueryRequest queryRequest, Class<E> executorType, Class<R> resultType) {
        Result result = executeAndRead(queryRequest, executorType);
        if (result == null || result.getAffectRow() <= 0) {
            return Collections.emptyList();
        }
        ResultParser resultParser = context.getResultParser();
        return resultParser.parse(result.getRawResults(), resultType);
    }


    /**
     * @param queryRequest
     * @param executorType
     * @param <E>
     * @return
     */
    @Override
    public <E extends AbstractSqlExecutor> int executeAndGetAffect(AbstractQueryRequest queryRequest, Class<E> executorType) {
        Result result = executeAndRead(queryRequest, executorType);
        return result.getAffectRow();
    }

}
