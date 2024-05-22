package org.devops.data.xjpa.configuration;

import org.devops.data.xjpa.datasource.RepositoryDataSource;
import org.devops.data.xjpa.table.EntityTable;
import org.springframework.util.ClassUtils;


@SuppressWarnings("rawtypes")
public class DefaultRepositoryProperties implements RepositoryProperties {

    private final EntityTable entityTable;

    private final RepositoryDataSource dataSource;

    public DefaultRepositoryProperties(EntityTable entityTable, RepositoryDataSource dataSource) {
        this.entityTable = entityTable;
        this.dataSource = dataSource;
    }

    @Override
    public String locatePackage() {
        return ClassUtils.getPackageName(getEntityTable().getEntityType());
    }

    @Override
    public RepositoryDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public EntityTable getEntityTable() {
        return entityTable;
    }


    @Override
    public String toString() {
        return "DefaultRepositoryProperties{" +
                "entityTable=" + entityTable +
                ", dataSource=" + dataSource +
                '}';
    }
}