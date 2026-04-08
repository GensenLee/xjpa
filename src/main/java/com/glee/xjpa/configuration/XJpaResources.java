package com.glee.xjpa.configuration;

import com.glee.xjpa.table.TableProperties;

/**
 * @author GENSEN
 * @date 2026/1/20
 * @description 全局管理器
 */
@SuppressWarnings("rawtypes")
public interface XJpaResources {

    TableProperties getTableProperties(Class<?> repositoryClass);

    TableProperties getTableProperties(String tableName);


}
