package com.glee.xjpa.lifecycle;

import com.glee.xjpa.repository.StandardJpaRepository;

import java.util.Collection;

/**
 * @author GENSEN
 * @date 2022/11/19
 * @description 注册器
 */
@SuppressWarnings("rawtypes")
public interface XjpaRepositoryRegister {


    /**
     * @param repositoryDefinition
     * @return
     */
    String register(RepositoryDefinition repositoryDefinition);

    /**
     * @return 全部已经注册的
     */
    Collection<StandardJpaRepository> registeredRepositories();

    /**
     * 获取已经注册的bean
     * @param repositoryType
     * @return
     */
    StandardJpaRepository getRegisteredRepository(Class repositoryType);


    boolean contains(Class repositoryType);

    StandardJpaRepository findRegisteredRepositoryOfEntity(Class entityType);

}
