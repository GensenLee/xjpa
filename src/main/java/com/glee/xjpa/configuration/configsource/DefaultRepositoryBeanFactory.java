package com.glee.xjpa.configuration.configsource;

/**
 * @author GENSEN
 * @date 2022/11/24
 * @description 默认数据源配置
 */
public class DefaultRepositoryBeanFactory implements RepositoryBeanFactory{

    protected final String[] baseScanPackages;

    protected final String dataSourceName;

    public DefaultRepositoryBeanFactory(String[] baseScanPackages, String dataSourceName) {
        this.baseScanPackages = baseScanPackages;
        this.dataSourceName = dataSourceName;
    }

    @Override
    public String[] baseScanPackages() {
        return baseScanPackages;
    }

    @Override
    public String dataSourceName() {
        return dataSourceName;
    }



}
