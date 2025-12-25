package com.glee.xjpa.repository.impl.proxy;

import com.glee.xjpa.configuration.RepositoryProperties;
import com.glee.xjpa.repository.StandardJpaRepository;
import com.glee.xjpa.repository.impl.*;
import com.glee.xjpa.util.ParameterizedTypeUtil;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Proxy;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description jdk 代理 + 委托模式
 */
public class JdkDelegateModeXjpaRepositoryBeanProxy implements XjpaRepositoryBeanProxy {


    private final RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory;

    private final ParameterizedTypeUtil parameterizedTypeUtil = new ParameterizedTypeUtil(StandardJpaRepository.class);

    private final FacadeCreatedHandle facadeCreatedHandle;

    public JdkDelegateModeXjpaRepositoryBeanProxy(RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory) {
        this.implProxyBeanFactoryFactory = implProxyBeanFactoryFactory;
        this.facadeCreatedHandle = new DisposeFacadeCreatedHandle();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object getProxy(Class<?> repositoryType, RepositoryProperties repositoryProperties) {
        RepositoryProxyBeanFactory beanFactory = implProxyBeanFactoryFactory.getFactory(StandardJpaRepository.class, null);

        Class keyType = (Class)parameterizedTypeUtil.getParameterizedType(repositoryType, 0);
        Class entityType = (Class)parameterizedTypeUtil.getParameterizedType(repositoryType, 1);


        ThreadLocalRepositoryDelegateHolder repositoryDelegate = new ThreadLocalRepositoryDelegateHolder(beanFactory, repositoryType);

        StandardJpaRepositoryFacade standardJpaRepositoryFacade = new StandardJpaRepositoryFacade(keyType, entityType, repositoryDelegate);

        standardJpaRepositoryFacade = facadeCreatedHandle.handle(standardJpaRepositoryFacade);

        return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), new Class<?>[]{repositoryType},
                new RepositoryInvocationHandler(repositoryType, standardJpaRepositoryFacade));
    }


}
