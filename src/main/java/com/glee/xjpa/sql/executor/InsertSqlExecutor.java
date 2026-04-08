package com.glee.xjpa.sql.executor;

import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.exception.XJpaExecuteException;
import com.glee.xjpa.io.InsertQueryRequest;
import com.glee.xjpa.io.QueryRequest;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.table.TableProperties;
import com.glee.xjpa.util.PreparedStatementUtil;
import com.glee.xjpa.util.PstParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2026/3/26
 * @description
 */
public class InsertSqlExecutor<K extends Serializable, E> {

    private final static Logger log = LoggerFactory.getLogger(InsertSqlExecutor.class);


    private final TableProperties<K, E> tableProperties;

    private final DataSourceManager dataSourceManager;

    private final SqlLogger sqlLogger;


    public InsertSqlExecutor(TableProperties<K, E> tableProperties, DataSourceManager dataSourceManager, SqlLogger sqlLogger) {
        this.tableProperties = tableProperties;
        this.dataSourceManager = dataSourceManager;
        this.sqlLogger = sqlLogger;
    }


    public int execute(InsertQueryRequest queryRequest) {


        String sqlTemplate = queryRequest.getSqlTemplate();
        sqlLogger.logSql(sqlTemplate);
        List<Map<Integer, PstParameter>> parameterList = queryRequest.getParameters();
        Connection connection = dataSourceManager.getConnection(tableProperties.getRepositoryType());
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlTemplate)) {
            for (Map<Integer, PstParameter> parameters : parameterList) {
                parameters.forEach((index, val) -> PreparedStatementUtil.setParameter(preparedStatement, val, index));
                preparedStatement.addBatch();
            }
            long start = System.currentTimeMillis();
            int[] batchAffect = preparedStatement.executeBatch();
            int affect = Arrays.stream(batchAffect).sum();
            sqlLogger.logAffect(affect, start, System.currentTimeMillis());
            return affect;
        } catch (Exception e) {
            log.error("sql execute fail, table = {}", tableProperties.getMetadata().getTableName(), e);
            throw new XJpaExecuteException("sql execute error", e);
        } finally {
            dataSourceManager.releaseConnection(connection, tableProperties.getRepositoryType());
        }
    }

}
