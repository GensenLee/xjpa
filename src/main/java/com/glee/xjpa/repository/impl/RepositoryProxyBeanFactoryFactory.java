package com.glee.xjpa.repository.impl;

import java.lang.reflect.Type;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 代理bean工厂的工厂
 */
@SuppressWarnings({"rawtypes"})
public interface RepositoryProxyBeanFactoryFactory {


    /**
     * @param beanType 根据需要创建的bean的类型获取工厂
     * @return
     */
    RepositoryProxyBeanFactory getFactory(Type beanType, RepositoryContext context);


}
