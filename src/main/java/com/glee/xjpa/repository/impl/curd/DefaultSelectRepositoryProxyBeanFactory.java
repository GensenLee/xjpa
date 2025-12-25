package com.glee.xjpa.repository.impl.curd;

import com.glee.xjpa.configuration.RepositoriesConfigurationManager;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description SelectRepositoryProxyImpl默认工厂
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultSelectRepositoryProxyBeanFactory extends AbstractRepositoryProxyBeanFactory<SelectRepositoryProxyImpl> {

    protected DefaultSelectRepositoryProxyBeanFactory(RepositoriesConfigurationManager repositoriesConfigurationManager) {
        super(repositoriesConfigurationManager);
    }

    @Override
    public SelectRepositoryProxyImpl getProxy(Class repositoryType) {
        EnhanceCurdBound enhanceCurdBound = context.getSingleton(EnhanceCurdBound.class);
        return new SelectRepositoryProxyImpl(context, enhanceCurdBound);
    }
}
