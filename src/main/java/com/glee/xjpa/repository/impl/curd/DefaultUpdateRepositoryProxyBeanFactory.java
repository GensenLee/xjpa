package com.glee.xjpa.repository.impl.curd;

import com.glee.xjpa.configuration.RepositoriesConfigurationManager;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description UpdateRepositoryProxyImpl默认工厂
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultUpdateRepositoryProxyBeanFactory extends AbstractRepositoryProxyBeanFactory<UpdateRepositoryProxyImpl> {

    protected DefaultUpdateRepositoryProxyBeanFactory(RepositoriesConfigurationManager repositoriesConfigurationManager) {
        super(repositoriesConfigurationManager);
    }

    @Override
    public UpdateRepositoryProxyImpl getProxy(Class repositoryType) {
        EnhanceCurdBound enhanceCurdBound = context.getSingleton(EnhanceCurdBound.class);
        return new UpdateRepositoryProxyImpl(context, enhanceCurdBound);
    }
}
