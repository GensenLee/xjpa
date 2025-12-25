package com.glee.xjpa.configuration.configsource;

/**
 * @author GENSEN
 * @date 2022/11/15
 * @description repository bean扫描工厂
 */
public interface RepositoryBeanFactory {

    /**
     * @return 扫描包
     */
    String[] baseScanPackages();

    /**
     * @return 包数据源
     */
    String dataSourceName();

}
