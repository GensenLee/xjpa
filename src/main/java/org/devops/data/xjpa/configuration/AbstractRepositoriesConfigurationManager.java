package org.devops.data.xjpa.configuration;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description 默认配置管理
 */
public abstract class AbstractRepositoriesConfigurationManager implements RepositoriesConfigurationManager {

    protected final RepositoryGlobalConfig repositoryGlobalConfig;

    protected AbstractRepositoriesConfigurationManager(RepositoryGlobalConfig repositoryGlobalConfig) {
        this.repositoryGlobalConfig = repositoryGlobalConfig;
    }

    @Override
    public RepositoryGlobalConfig getGlobalConfig() {
        return repositoryGlobalConfig;
    }
}
