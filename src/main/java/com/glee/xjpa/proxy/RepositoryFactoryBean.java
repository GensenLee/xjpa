package com.glee.xjpa.proxy;

import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.io.StandardXJpaRepository;
import com.glee.xjpa.sql.logger.DefaultSqlLogger;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.table.TableProperties;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.lang.reflect.Proxy;

@SuppressWarnings("unchecked")
public class RepositoryFactoryBean<T extends StandardXJpaRepository<K, E>, K extends Serializable, E> implements FactoryBean<T>, InitializingBean {

    private final Class<T> repositoryClass;
    private final TableProperties<K, E> tableProperties;
    private final DataSourceManager dataSourceManager;
    private T repositoryProxy;


    public RepositoryFactoryBean(Class<T> repositoryClass, TableProperties<K, E> tableProperties,
                                 DataSourceManager dataSourceManager) {
        this.repositoryClass = repositoryClass;
        this.tableProperties = tableProperties;
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public T getObject() {
        return repositoryProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return repositoryClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() {
        SqlLogger sqlLogger = new DefaultSqlLogger();
        XJpaStandardRepositoryImpl<K, E> repositoryImpl = new XJpaStandardRepositoryImpl<>(
                tableProperties, dataSourceManager, sqlLogger);

        // 使用动态代理创建Repository实例
        RepositoryInvocationHandler<K, E> proxyHandler = new RepositoryInvocationHandler<>(repositoryImpl);
        this.repositoryProxy = (T) Proxy.newProxyInstance(
                repositoryClass.getClassLoader(),
                new Class[]{repositoryClass},
                proxyHandler
        );
    }

}