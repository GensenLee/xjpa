package com.glee.xjpa.configuration.configsource;

import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/15
 * @description bean配置源
 */
public interface XjpaRepositoryBeanConfig {

    /**
     * @return packages -> dataSourceName
     */
    Map<String[], String> scanPackages();

}
