package org.devops.data.xjpa.sql.executor.session;

import lombok.extern.slf4j.Slf4j;
import org.devops.data.xjpa.datasource.RepositoryDataSource;
import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.data.xjpa.repository.invocation.GlobalRepositoryInvocationHandler;
import org.springframework.transaction.NoTransactionException;

import javax.persistence.TransactionRequiredException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/7
 * @description 基于model context控制
 */
@Slf4j
public class DataSourceExecuteSession implements ExecuteSession {

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
            log.error("create PreparedStatement error, sql: {}", sql);
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
            log.error("create statement error, sql: {}", sql, e);
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
            log.error("requireTransactionEnabled error", e);
        }
        if (!GlobalRepositoryInvocationHandler.get().isTransactionHandled()) {
            log.error("A write operation forces the start of a transaction");
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
            log.trace("closing connection");
            for (PreparedStatement preparedStatement : localStatementList) {
                if (!preparedStatement.isClosed()) {
                    preparedStatement.close();
                }
            }
            datasource.close();
        } catch (Exception e) {
            log.error("connection close error", e);
        } finally {
            localStatementList.clear();
        }
    }
}
