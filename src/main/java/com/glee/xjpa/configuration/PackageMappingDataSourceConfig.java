package com.glee.xjpa.configuration;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/15
 * @description 根据包路径的数据源配置方式
 */
public class PackageMappingDataSourceConfig implements XJpaDataSourceConfig {

    private final Map<String, String> packageDataSourceNameMapping;

    public PackageMappingDataSourceConfig() {
        this.packageDataSourceNameMapping = new HashMap<>();
    }

    /**
     * 绑定包数据源
     * @param dataSourceName
     * @param packages
     */
    public PackageMappingDataSourceConfig bind(String dataSourceName, String... packages) {
        if (packages.length == 0) {
            return this;
        }
        for (String pkg : packages) {
            if (StrUtil.isNotBlank(pkg)) {
                packageDataSourceNameMapping.put(pkg, dataSourceName);
            }
        }
        return this;
    }


    /**
     * @return full package name -> data source name
     */
    @Override
    public Map<String, String> getPackageDataSourceMapping() {
        return packageDataSourceNameMapping;
    }
}
