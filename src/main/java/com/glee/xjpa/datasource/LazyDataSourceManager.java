package com.glee.xjpa.datasource;

import com.glee.xjpa.configuration.XJpaResources;
import com.glee.xjpa.table.TableProperties;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author GENSEN
 * @date 2026/3/24
 * @description
 */
public class LazyDataSourceManager implements DataSourceManager {

    private final XJpaResources xJpaResources;

    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public LazyDataSourceManager(XJpaResources xJpaResources, DefaultListableBeanFactory defaultListableBeanFactory) {
        this.xJpaResources = xJpaResources;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Override
    public Connection getConnection(Class<?> repositoryClass) {
        DataSource dataSource = getDataSource(repositoryClass);
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public void releaseConnection(Connection connection, Class<?> repositoryClass) {
        DataSource dataSource = getDataSource(repositoryClass);
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    private DataSource getDataSource(Class<?> repositoryClass) {
        TableProperties<?, ?> tableProperties = xJpaResources.getTableProperties(repositoryClass);
        String dataSourceName = tableProperties.getDataSourceName();
        return defaultListableBeanFactory.getBean(dataSourceName, DataSource.class);
    }
}
