package com.glee.xjpa.datasource;

import java.sql.Connection;

/**
 * @author GENSEN
 * @date 2026/3/24
 * @description
 */
public interface DataSourceManager {

    Connection getConnection(Class<?> repositoryClass);

    void releaseConnection(Connection connection, Class<?> repositoryClass);

}
