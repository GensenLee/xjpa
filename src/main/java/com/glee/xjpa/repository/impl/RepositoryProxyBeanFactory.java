package com.glee.xjpa.repository.impl;

import com.glee.xjpa.repository.IXjpaRepository;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description repository标准工厂接口
 */
@SuppressWarnings("rawtypes")
public interface RepositoryProxyBeanFactory<T extends IXjpaRepository> {

    /**
     * @param repositoryType
     * @return
     */
    T getProxy(Class<?> repositoryType);

    void bindContext(RepositoryContext context);

}
