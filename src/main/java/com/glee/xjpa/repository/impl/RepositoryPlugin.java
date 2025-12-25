package com.glee.xjpa.repository.impl;

import cn.hutool.core.date.StopWatch;
import com.glee.xjpa.exception.XjpaExecuteException;
import com.glee.xjpa.repository.StandardJpaRepository;
import com.glee.xjpa.repository.impl.proxy.RepositoryInvocationHandler;
import com.glee.xjpa.sql.executor.session.ExecuteSession;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.sql.logger.SwitchableLogger;
import com.glee.xjpa.table.TableMetadata;
import com.glee.xjpa.table.identifier.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @author GENSEN
 * @date 2022/11/19
 * @description repository控制器
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class RepositoryPlugin implements RepositoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryPlugin.class);


    private final Object repository;

    /**
     * 实际接收的的对象
     */
    private final Object actualRepository;

    public <T extends StandardJpaRepository> RepositoryPlugin(T repository) {
        this.repository = repository;
        if (Proxy.isProxyClass(repository.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(repository);
            actualRepository = ((RepositoryInvocationHandler) invocationHandler).getStandardJpaRepository();
        }else {
            actualRepository = repository;
        }
    }


    @Override
    public void setAttribute(String key, Object attribute) {

        Object target = getTarget();

        if (!(target instanceof RepositoryContextAttribute)) {
            return;
        }

        ((RepositoryContextAttribute) target).setAttribute(key, attribute);
    }

    @Override
    public Object getAttribute(String key) {
        Object target = getTarget();

        if (!(target instanceof RepositoryContextAttribute)) {
            return null;
        }

        return  ((RepositoryContextAttribute) target).getAttribute(key);
    }

    @Override
    public <K> K getNextId() {
        Object target = getTarget();

        if (target instanceof RepositoryContext) {
            IdentifierGenerator identifierGenerator = ((RepositoryContext<?, ?>) target).getSingleton(IdentifierGenerator.class);
            return (K) identifierGenerator.next();
        }

        return null;
    }

    @Override
    public RepositoryContext getContext() {
        Object target = getTarget();

        if (target instanceof RepositoryContext) {
            return ((RepositoryContext<?, ?>) target);
        }

        throw new IllegalStateException("should not reach here");
    }

    private Object getTarget() {
        Object target;
        if (actualRepository instanceof Facade) {
            target = ((Facade) actualRepository).getActual();
        }else {
            target = actualRepository;
        }
        return target;
    }

    @Override
    public TableMetadata getTableFieldMetadata() {
        Object target = getTarget();

        RepositoryContext context = (RepositoryContext) target;

        return context.getEntityTable();
    }

    @Override
    public void closeLog() {
        Object target = getTarget();

        RepositoryContext context = (RepositoryContext) target;

        SqlLogger sqlLogger = context.localSqlLogger();

        if (sqlLogger instanceof SwitchableLogger) {
            ((SwitchableLogger) sqlLogger).closeLog();
        }
    }

    @Override
    public void openLog() {
        Object target = getTarget();

        RepositoryContext context = (RepositoryContext) target;

        SqlLogger sqlLogger = context.localSqlLogger();

        if (sqlLogger instanceof SwitchableLogger) {
            ((SwitchableLogger) sqlLogger).openLog();
        }
    }

    /**
     * 执行sql语句并读取结果
     * @param sql
     * @return
     */
    public List<Map<String, Object>> rawQuery(String sql) {

        ExecuteSession executeSession = getContext().localSessionManager();

        StopWatch stopWatch = new StopWatch("SqlExecutorUtil#query");
        stopWatch.start("executeSession.readStatement(sql)");
        try (PreparedStatement preparedStatement = executeSession.readStatement(sql)){
            stopWatch.stop();
            stopWatch.start("preparedStatement.executeQuery()");
            ResultSet resultSet = preparedStatement.executeQuery();
            stopWatch.stop();
            stopWatch.start("read resultSet");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<Map<String, Object>> result = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    Object value = resultSet.getObject(i);
                    if (row.containsKey(columnLabel)) {
                        row.put(createAlias(columnLabel, row.keySet()), value);
                    } else {
                        row.put(columnLabel, value);
                    }
                }
                result.add(row);
            }
            resultSet.close();
            stopWatch.stop();
            return result;
        } catch (SQLException e) {
            throw new XjpaExecuteException(e);
        } finally {
            getContext().dispose();
            logger.trace("query time use\n" + stopWatch.prettyPrint());
        }
    }

    private static String createAlias(String columnName, Set<String> keySet) {
        if (!keySet.contains(columnName)) {
            return columnName;
        }

        return createAlias(columnName + "_1", keySet);
    }
}
