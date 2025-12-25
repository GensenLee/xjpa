package com.glee.xjpa.configuration.configsource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/15
 * @description 默认bean配置
 */
public class DefaultXjpaRepositoryBeanConfig implements XjpaRepositoryBeanConfig {

    private final Map<String[], String> packageDataSourceNameMap;

    public DefaultXjpaRepositoryBeanConfig() {
        this.packageDataSourceNameMap = new HashMap<>();
    }

    /**
     * 绑定包数据源
     * @param dataSourceName
     * @param packages
     */
    public DefaultXjpaRepositoryBeanConfig bind(String dataSourceName, String... packages) {
        if (packages.length == 0) {
            return this;
        }
        packageDataSourceNameMap.put(packages, dataSourceName);
        return this;
    }


    @Override
    public Map<String[], String> scanPackages() {
        return packageDataSourceNameMap;
    }
}
