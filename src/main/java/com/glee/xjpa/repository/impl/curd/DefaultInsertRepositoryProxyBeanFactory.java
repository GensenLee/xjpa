package com.glee.xjpa.repository.impl.curd;

import com.glee.xjpa.configuration.RepositoriesConfigurationManager;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description InsertRepositoryProxyImpl默认工厂
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultInsertRepositoryProxyBeanFactory extends AbstractRepositoryProxyBeanFactory<InsertRepositoryProxyImpl> {

    protected DefaultInsertRepositoryProxyBeanFactory(RepositoriesConfigurationManager repositoriesConfigurationManager) {
        super(repositoriesConfigurationManager);
    }

    @Override
    public InsertRepositoryProxyImpl getProxy(Class repositoryType) {
        return new InsertRepositoryProxyImpl(context);
    }
}
