package com.glee.xjpa.configuration;

import com.glee.xjpa.datasource.RepositoryDataSource;
import com.glee.xjpa.table.EntityTable;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 配置
 */
public interface RepositoryProperties {

    /**
     * @return repository 所在包
     */
    String locatePackage();

    /**
     * @return
     */
    RepositoryDataSource getDataSource();

    /**
     * @return
     */
    EntityTable getEntityTable();
}
