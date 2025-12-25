package com.glee.xjpa.sql.executor.session;

import com.glee.xjpa.datasource.RepositoryDataSource;
import com.glee.xjpa.exception.XjpaExecuteException;
import com.glee.xjpa.repository.invocation.GlobalRepositoryInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.TransactionRequiredException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/7
 * @description 基于model context控制
 */
public class DataSourceExecuteSession implements ExecuteSession {
    protected static final Logger logger = LoggerFactory.getLogger(DataSourceExecuteSession.class);

    private final RepositoryDataSource datasource;

    private final List<PreparedStatement> localStatementList;


    public DataSourceExecuteSession(RepositoryDataSource datasource) {
        this.datasource = datasource;
        this.localStatementList = new ArrayList<>();
    }


    @Override
    public PreparedStatement readStatement(String sql) {
        try {
            Connection connection = datasource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            localStatementList.add(preparedStatement);
            return preparedStatement;
        } catch (SQLException e) {
            logger.error("create PreparedStatement error, sql: {}", sql);
            throw new XjpaExecuteException(e);
        }
    }

    @Override
    public PreparedStatement updateStatement(String sql) {
        try {
            Connection connection = datasource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            localStatementList.add(preparedStatement);
            return preparedStatement;
        } catch (SQLException e) {
            logger.error("create statement error, sql: {}", sql, e);
            throw new XjpaExecuteException(e);
        }
    }

    /**
     * 内部存在批量写操作时需要强制开启事务
     */
    @Override
    public void requireTransactionEnabled() {
        try {
            if (!datasource.getConnection().getAutoCommit()) {
                return;
            }
        } catch (SQLException e) {
            logger.error("requireTransactionEnabled error", e);
        }
        if (!GlobalRepositoryInvocationHandler.get().isTransactionHandled()) {
            logger.error("A write operation forces the start of a transaction");
            throw new TransactionRequiredException("require transaction enabled");
        }
    }

    @Override
    public Connection getConnection(boolean closeTransaction) {
        return datasource.getConnection(closeTransaction);
    }

    @Override
    public DataSource getDataSource() {
        return datasource.getDataSource();
    }


    @Override
    public void dispose() {

        try {
            logger.trace("closing connection");
            for (PreparedStatement preparedStatement : localStatementList) {
                if (!preparedStatement.isClosed()) {
                    preparedStatement.close();
                }
            }
            datasource.close();
        } catch (Exception e) {
            logger.error("connection close error", e);
        } finally {
            localStatementList.clear();
        }
    }
}
