package com.glee.xjpa.configuration.configsource;

/**
 * @author GENSEN
 * @date 2022/11/24
 * @description
 */
public final class RepositoryBeanFactoryBuilder {
    private String[] baseScanPackages;
    private String dataSourceName;

    private RepositoryBeanFactoryBuilder() {
    }

    public static RepositoryBeanFactoryBuilder builder() {
        return new RepositoryBeanFactoryBuilder();
    }

    public RepositoryBeanFactoryBuilder withBaseScanPackages(String... baseScanPackages) {
        this.baseScanPackages = baseScanPackages;
        return this;
    }

    public RepositoryBeanFactoryBuilder withDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        return this;
    }

    public RepositoryBeanFactory build() {
        return new DefaultRepositoryBeanFactory(baseScanPackages, dataSourceName);
    }
}
