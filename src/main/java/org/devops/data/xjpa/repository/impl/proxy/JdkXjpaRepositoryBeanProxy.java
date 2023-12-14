package org.devops.data.xjpa.repository.impl.proxy;

import org.devops.data.xjpa.configuration.RepositoryProperties;
import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.devops.data.xjpa.repository.impl.RepositoryProxyBeanFactory;
import org.devops.data.xjpa.repository.impl.RepositoryProxyBeanFactoryFactory;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Proxy;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 默认
 */
public class JdkXjpaRepositoryBeanProxy implements XjpaRepositoryBeanProxy {

    private final RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory;

    public JdkXjpaRepositoryBeanProxy(RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory) {
        this.implProxyBeanFactoryFactory = implProxyBeanFactoryFactory;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object getProxy(Class<?> repositoryType, RepositoryProperties repositoryProperties) {

        RepositoryProxyBeanFactory beanFactory = implProxyBeanFactoryFactory.getFactory(StandardJpaRepository.class, null);

        StandardJpaRepository repositoryProxy = (StandardJpaRepository) beanFactory.getProxy(repositoryType);


        return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), new Class<?>[]{repositoryType},
                new RepositoryInvocationHandler(repositoryType, repositoryProxy));
    }


}
