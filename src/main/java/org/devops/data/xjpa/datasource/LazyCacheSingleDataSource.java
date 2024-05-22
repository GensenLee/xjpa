package org.devops.data.xjpa.datasource;

import org.devops.data.xjpa.configuration.SpringApplicationContextHandle;
import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.data.xjpa.repository.invocation.GlobalRepositoryInvocationHandler;
import org.devops.data.xjpa.repository.invocation.RepositoryInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 数据源对象缓存模式
 */
public class LazyCacheSingleDataSource implements RepositoryDataSource {

    protected static final Logger logger = LoggerFactory.getLogger(LazyCacheSingleDataSource.class);

    private DataSource dataSource;

    private final String dataSourceName;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private final ThreadLocal<Connection> connectionLocal = new InheritableThreadLocal<>();

    public LazyCacheSingleDataSource(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }


    protected DataSource getDatasource() {
        if (dataSource == null) {
            doGet();
        }

        return dataSource;
    }

    @Override
    public Connection getConnection(boolean closeTransaction) {
        Connection connection = connectionLocal.get();
        if (connection != null) {
            try {
                if (connection.isClosed()) {
                    connectionLocal.remove();
                    return getConnection(closeTransaction);
                }
            } catch (SQLException e) {
                logger.error("connection close", e);
            }
            return connection;
        }
        if (closeTransaction) {
            try {
                connection = getDatasource().getConnection();
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                logger.error("open connection error", e);
                throw new XjpaExecuteException(e.getMessage());
            }
        }else {
            // 使用 DataSourceUtils.getConnection 才能借助spring管理事务
            connection = DataSourceUtils.getConnection(getDatasource());
        }
        connectionLocal.set(connection);
        return connection;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void close() {
        Connection connection = connectionLocal.get();
        connectionLocal.remove();
        if (connection == null) {
            return;
        }

        try {
            RepositoryInvocation invocation = GlobalRepositoryInvocationHandler.get();
            if (invocation != null && !invocation.isTransactionHandled()) {
                DataSourceUtils.doReleaseConnection(connection, dataSource);
            }
        } catch (SQLException e) {
            throw new XjpaExecuteException(e);
        }finally {
            connectionLocal.remove();
        }
    }

    private void doGet() {
        reentrantLock.lock();

        try {
            dataSource = (DataSource) SpringApplicationContextHandle.getApplicationContext().getBean(dataSourceName);
        } catch (Exception e) {
            logger.error("获取数据源失败 dataSourceName={}", dataSourceName);
            throw new RuntimeException(e);
        }

        reentrantLock.unlock();
    }

    @Override
    public String toString() {
        return "LazyCacheSingleDataSource{" +
                "dataSource=" + dataSource +
                ", dataSourceName='" + dataSourceName + '\'' +
                '}';
    }
}
