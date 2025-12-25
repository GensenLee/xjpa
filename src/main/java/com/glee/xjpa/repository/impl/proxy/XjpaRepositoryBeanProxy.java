package com.glee.xjpa.repository.impl.proxy;

import com.glee.xjpa.configuration.RepositoryProperties;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description RepositoryProxy工厂
 */
public interface XjpaRepositoryBeanProxy {


    Object getProxy(Class<?> repositoryType, RepositoryProperties repositoryProperties);

}
