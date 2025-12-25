package com.glee.xjpa.repository.impl.curd;

import com.glee.xjpa.configuration.RepositoriesConfigurationManager;
import com.glee.xjpa.repository.IDeleteRepository;
import com.glee.xjpa.repository.IInsertRepository;
import com.glee.xjpa.repository.ISelectRepository;
import com.glee.xjpa.repository.IUpdateRepository;
import com.glee.xjpa.repository.impl.RepositoryProxyBeanFactory;
import com.glee.xjpa.repository.impl.RepositoryProxyBeanFactoryFactory;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description StandardCurdRepositoryProxyImpl默认工厂
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultStandardCurdRepositoryProxyBeanFactory extends AbstractRepositoryProxyBeanFactory<EnhanceCurdRepositoryProxyImpl> {


    private final RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory;

    protected DefaultStandardCurdRepositoryProxyBeanFactory(RepositoriesConfigurationManager repositoriesConfigurationManager,
                                                            RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory) {
        super(repositoriesConfigurationManager);
        this.implProxyBeanFactoryFactory = implProxyBeanFactoryFactory;
    }

    /**
     * @param repositoryType
     * @return
     */
    @Override
    public EnhanceCurdRepositoryProxyImpl getProxy(Class repositoryType) {


        RepositoryProxyBeanFactory deleteRepositoryProxyBeanFactory =
                implProxyBeanFactoryFactory.getFactory(IDeleteRepository.class, context);

        RepositoryProxyBeanFactory insertRepositoryProxyBeanFactory =
                implProxyBeanFactoryFactory.getFactory(IInsertRepository.class, context);

        RepositoryProxyBeanFactory selectRepositoryProxyBeanFactory =
                implProxyBeanFactoryFactory.getFactory(ISelectRepository.class, context);

        RepositoryProxyBeanFactory updateRepositoryProxyBeanFactory =
                implProxyBeanFactoryFactory.getFactory(IUpdateRepository.class, context);

        EnhanceCurdBound enhanceCurdBound = context.getSingleton(EnhanceCurdBound.class);

        EnhanceCurdRepositoryProxyImpl standardCurdRepositoryProxy = new EnhanceCurdRepositoryProxyImpl(
                context,
                enhanceCurdBound,
                doGetProxy(repositoryType, selectRepositoryProxyBeanFactory),
                doGetProxy(repositoryType, insertRepositoryProxyBeanFactory),
                doGetProxy(repositoryType, updateRepositoryProxyBeanFactory),
                doGetProxy(repositoryType, deleteRepositoryProxyBeanFactory));

        return standardCurdRepositoryProxy;
    }


    /**
     * @param repositoryType
     * @param proxyBeanFactory
     * @param <T>
     * @return
     */
    private <T> T doGetProxy(Class repositoryType, RepositoryProxyBeanFactory proxyBeanFactory) {
        return (T) proxyBeanFactory.getProxy(repositoryType);
    }
}
