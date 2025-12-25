package com.glee.xjpa.repository.impl.curd;

import com.glee.xjpa.configuration.RepositoriesConfigurationManager;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description SingleColumnSupportSelectRepositoryProxyImpl工厂
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SingleColumnSupportSelectRepositoryProxyBeanFactory extends AbstractRepositoryProxyBeanFactory<SingleColumnSupportSelectRepositoryProxyImpl> {

    protected SingleColumnSupportSelectRepositoryProxyBeanFactory(RepositoriesConfigurationManager repositoriesConfigurationManager) {
        super(repositoriesConfigurationManager);
    }

    @Override
    public SingleColumnSupportSelectRepositoryProxyImpl getProxy(Class repositoryType) {
        EnhanceCurdBound enhanceCurdBound = context.getSingleton(EnhanceCurdBound.class);
        return new SingleColumnSupportSelectRepositoryProxyImpl(context, enhanceCurdBound);
    }
}
