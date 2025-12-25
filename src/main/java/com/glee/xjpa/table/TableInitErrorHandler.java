package com.glee.xjpa.table;

import com.glee.xjpa.configuration.RepositoryProperties;

/**
 * @author GENSEN
 * @date 2023/1/4
 * @description 表初始化异常处理
 */
public interface TableInitErrorHandler {


    /**
     * @param repositoryType
     * @param tableName
     * @param repositoryProperties
     * @param exception
     * @return 是否重试
     */
    boolean handle(Class<?> repositoryType, String tableName, RepositoryProperties repositoryProperties, Exception exception);

}
