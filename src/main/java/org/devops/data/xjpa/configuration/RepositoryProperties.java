package org.devops.data.xjpa.configuration;

import org.devops.data.xjpa.datasource.RepositoryDataSource;
import org.devops.data.xjpa.table.EntityTable;

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
