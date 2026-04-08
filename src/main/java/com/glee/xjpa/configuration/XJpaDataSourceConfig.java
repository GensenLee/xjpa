package com.glee.xjpa.configuration;

import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/15
 * @description bean配置源
 */
public interface XJpaDataSourceConfig {

    /**
     * @return package -> dataSourceName
     */
    Map<String, String> getPackageDataSourceMapping();

}
