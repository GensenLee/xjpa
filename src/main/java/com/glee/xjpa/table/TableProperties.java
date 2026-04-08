package com.glee.xjpa.table;


import com.glee.xjpa.io.StandardXJpaRepository;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2026-03-23
 * @description 配置
 */
public class TableProperties<K extends Serializable, E> {

    private final Class<? extends StandardXJpaRepository<K, E>> repositoryClass;

    private final XJpaTableMetadata<K, E> metadata;

    private final String dataSourceName;

    public TableProperties(Class<? extends StandardXJpaRepository<K, E>> repositoryClass,
                           XJpaTableMetadata<K, E> metadata, String dataSourceName) {
        this.repositoryClass = repositoryClass;
        this.metadata = metadata;
        this.dataSourceName = dataSourceName;
    }

    public Class<? extends StandardXJpaRepository<K, E>> getRepositoryType() {
        return repositoryClass;
    }


    /**
     * @return
     */
    public XJpaTableMetadata<K, E> getMetadata() {
        return metadata;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public boolean isSoftDeleteEnabled() {
        return false;
    }
}
