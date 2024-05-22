package org.devops.data.xjpa.repository.impl.curd;

import org.devops.data.xjpa.configuration.RepositoriesConfigurationManager;
import org.devops.data.xjpa.repository.IDeleteRepository;
import org.devops.data.xjpa.repository.impl.RepositoryProxyBeanFactoryFactory;
import org.devops.data.xjpa.repository.impl.enhance.EnhanceCurdBound;
import org.devops.data.xjpa.sql.where.handler.SoftDeleteConfig;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description DeleteRepositoryProxyImpl默认工厂
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultDeleteRepositoryProxyBeanFactory extends AbstractRepositoryProxyBeanFactory<IDeleteRepository> {


    private final RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory;

    protected DefaultDeleteRepositoryProxyBeanFactory(RepositoriesConfigurationManager repositoriesConfigurationManager,
                                                      RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory) {
        super(repositoriesConfigurationManager);
        this.implProxyBeanFactoryFactory = implProxyBeanFactoryFactory;
    }

    @Override
    public IDeleteRepository getProxy(Class<?> repositoryType) {

        SoftDeleteConfig softDeleteConfig = context.getSingleton(SoftDeleteConfig.class);
        if (softDeleteConfig != null && softDeleteConfig.isEnabled()) {
            EnhanceCurdBound enhanceCurdBound = context.getSingleton(EnhanceCurdBound.class);
            return new SoftDeleteRepositoryProxyImpl(context, enhanceCurdBound, softDeleteConfig);
        }else {
            return new DeleteRepositoryProxyImpl(context);
        }
    }
}
