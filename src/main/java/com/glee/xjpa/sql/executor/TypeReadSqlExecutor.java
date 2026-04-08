package com.glee.xjpa.sql.executor;

import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.exception.XJpaExecuteException;
import com.glee.xjpa.io.QueryRequest;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.sql.result.ResultConverter;
import com.glee.xjpa.table.TableProperties;
import com.glee.xjpa.table.XJpaTableMetadata;
import com.glee.xjpa.util.PreparedStatementUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2026-04-01
 * @description
 */
public class TypeReadSqlExecutor<K extends Serializable, E> {

    private final static Logger log = LoggerFactory.getLogger(TypeReadSqlExecutor.class);


    private final TableProperties<K, E> tableProperties;

    private final DataSourceManager dataSourceManager;

    private final SqlLogger sqlLogger;


    public TypeReadSqlExecutor(TableProperties<K, E> tableProperties, DataSourceManager dataSourceManager,SqlLogger sqlLogger) {
        this.tableProperties = tableProperties;
        this.dataSourceManager = dataSourceManager;
        this.sqlLogger = sqlLogger;
    }

    public <T> List<T> execute(QueryRequest queryRequest, ResultConverter<T> converter) {
        String sqlTemplate = queryRequest.getSqlTemplate();
        sqlLogger.logSql(sqlTemplate);
        Map<Integer, Object> parameters = queryRequest.getParameters();
        XJpaTableMetadata<K, E> metadata = tableProperties.getMetadata();
        Connection connection = dataSourceManager.getConnection(tableProperties.getRepositoryType());
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlTemplate,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            parameters.forEach((index, val) -> PreparedStatementUtil.setParameter(preparedStatement, val, index));
            long start = System.currentTimeMillis();

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                sqlLogger.logResult(resultSet, start, System.currentTimeMillis());
                return converter.convert(resultSet);
            }
        } catch (SQLException e) {
            log.error("sql execute fail, table = {}", metadata.getTableName(), e);
            throw new XJpaExecuteException("sql execute error", e);
        } finally {
            dataSourceManager.releaseConnection(connection, tableProperties.getRepositoryType());
        }
    }
}
