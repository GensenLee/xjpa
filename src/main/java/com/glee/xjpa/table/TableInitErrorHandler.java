package com.glee.xjpa.table;

/**
 * @author GENSEN
 * @date 2023/1/4
 * @description 表初始化异常处理
 */
public interface TableInitErrorHandler {


    /**
     * @param repositoryClass
     * @param tableName
     * @param exception
     * @return 是否重试
     */
    boolean handle(Class<?> repositoryClass, String tableName, Exception exception);

}
