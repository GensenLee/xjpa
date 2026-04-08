package com.glee.xjpa.configuration;

import com.glee.xjpa.exception.XJpaException;
import com.glee.xjpa.table.TableProperties;

import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2026/1/20
 * @description
 */
@SuppressWarnings("rawtypes")
public class DefaultXJpaResources implements XJpaResources {

    private final XJpaProperties xJpaProperties;

    private final List<XJpaDataSourceConfig> dataSourceConfigList;

    private final Map<String, TableProperties> tablePropertiesMap;

    public DefaultXJpaResources(XJpaProperties xJpaProperties, List<XJpaDataSourceConfig> dataSourceConfigList,
                                Map<String, TableProperties> tablePropertiesMap) {
        this.xJpaProperties = xJpaProperties;
        this.dataSourceConfigList = dataSourceConfigList;
        this.tablePropertiesMap = tablePropertiesMap;
    }


    @Override
    public TableProperties getTableProperties(Class<?> repositoryClass) {
        return tablePropertiesMap.values()
                .stream()
                .filter(p -> p.getRepositoryType().equals(repositoryClass))
                .findFirst()
                .orElseThrow(() -> new XJpaException("type %s not exist".formatted(repositoryClass)));
    }

    @Override
    public TableProperties getTableProperties(String tableName) {
        return tablePropertiesMap.get(tableName);
    }
}
