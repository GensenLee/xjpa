package com.glee.xjpa.repository.impl.proxy;

import com.glee.xjpa.configuration.RepositoryProperties;
import com.glee.xjpa.repository.StandardJpaRepository;
import com.glee.xjpa.repository.impl.RepositoryProxyBeanFactory;
import com.glee.xjpa.repository.impl.RepositoryProxyBeanFactoryFactory;
import com.glee.xjpa.repository.impl.curd.DefaultStandardJpaRepositoryProxyBeanFactory;
import com.glee.xjpa.repository.impl.curd.StandardJpaRepositoryProxyImpl;
import com.glee.xjpa.sql.logger.SqlLogger;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description cglib
 */
public class CglibXjpaRepositoryBeanProxy implements XjpaRepositoryBeanProxy {

    private final RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory;

    public CglibXjpaRepositoryBeanProxy(RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory) {
        this.implProxyBeanFactoryFactory = implProxyBeanFactoryFactory;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object getProxy(Class<?> repositoryType, RepositoryProperties repositoryProperties) {
        RepositoryProxyBeanFactory beanFactory = implProxyBeanFactoryFactory.getFactory(StandardJpaRepository.class, null);
        StandardJpaRepositoryProxyImpl repositoryProxy = (StandardJpaRepositoryProxyImpl) beanFactory.getProxy(repositoryType);


        DefaultStandardJpaRepositoryProxyBeanFactory repositoryImplProxyBeanFactory = (DefaultStandardJpaRepositoryProxyBeanFactory) beanFactory;

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(StandardJpaRepositoryProxyImpl.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                try {
                    result = method.invoke(repositoryProxy, objects);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                } finally {
                    repositoryProxy.dispose();
                }
                return result;
            }
        });
        Class[] argumentTypes = new Class[]{Class.class, Class.class, SqlLogger.class, RepositoryProperties.class};
        Object[] arguments = new Object[]{repositoryProxy.getKeyType(), repositoryProxy.getEntityType(),
                repositoryImplProxyBeanFactory.getSqlLogger(repositoryProperties), repositoryProperties};
        return enhancer.create(argumentTypes, arguments);
    }


}
