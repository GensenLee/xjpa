package com.glee.xjpa.sql.executor;

import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.exception.XJpaExecuteException;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.util.PreparedStatementUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连表查询SQL执行器
 */
public class JoiningSqlExecutor {

    private final static Logger log = LoggerFactory.getLogger(JoiningSqlExecutor.class);

    private final DataSourceManager dataSourceManager;

    private final Class<?> repositoryClass;

    private final SqlLogger sqlLogger;

    public JoiningSqlExecutor(DataSourceManager dataSourceManager, Class<?> repositoryClass, SqlLogger sqlLogger) {
        this.dataSourceManager = dataSourceManager;
        this.repositoryClass = repositoryClass;
        this.sqlLogger = sqlLogger;
    }

    public List<Map<String, Object>> executeQuery(String sql) {
        sqlLogger.logSql(sql);
        Connection connection = dataSourceManager.getConnection(repositoryClass);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            long start = System.currentTimeMillis();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                sqlLogger.logResult(resultSet, start, System.currentTimeMillis());
                return convertResultSetToList(resultSet);
            }
        } catch (SQLException e) {
            log.error("sql execute fail, sql = {}", sql, e);
            throw new XJpaExecuteException("sql execute error", e);
        } finally {
            dataSourceManager.releaseConnection(connection, repositoryClass);
        }
    }

    public long executeCount(String sql) {
        sqlLogger.logSql(sql);
        Connection connection = dataSourceManager.getConnection(repositoryClass);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            long start = System.currentTimeMillis();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            log.error("sql execute fail, sql = {}", sql, e);
            throw new XJpaExecuteException("sql execute error", e);
        } finally {
            dataSourceManager.releaseConnection(connection, repositoryClass);
        }
    }

    private List<Map<String, Object>> convertResultSetToList(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = resultSet.getObject(i);
                row.put(columnName, value);
            }
            result.add(row);
        }

        return result;
    }
}