package com.glee.xjpa.lifecycle;

import com.glee.xjpa.configuration.EnvironmentRepositoriesConfigurationManager;
import com.glee.xjpa.configuration.RepositoriesConfigurationManager;
import com.glee.xjpa.configuration.RepositoryGlobalConfig;
import com.glee.xjpa.configuration.SpringRepositoryGlobalConfig;
import com.glee.xjpa.configuration.*;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description 默认配置源
 */
public class DefaultRepositoryConfigSource implements RepositoryConfigSource {

    private DefaultListableBeanFactory registry;

    private RepositoriesConfigurationManager repositoriesConfigurationManager;

    private RepositoryGlobalConfig repositoryGlobalConfig;

    private final Environment environment;

    public DefaultRepositoryConfigSource(Environment environment) {
        this.environment = environment;
    }

    @Override
    public synchronized void refresh() {


        repositoryGlobalConfig = new SpringRepositoryGlobalConfig(registry, environment);
        repositoryGlobalConfig.refresh();

        repositoriesConfigurationManager = new EnvironmentRepositoriesConfigurationManager(repositoryGlobalConfig, registry, environment);
        repositoriesConfigurationManager.refresh();
    }

    @Override
    public RepositoryGlobalConfig getGlobalConfig() {
        return repositoryGlobalConfig;
    }

    @Override
    public RepositoriesConfigurationManager getConfigManager() {
        return repositoriesConfigurationManager;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    void setRegistry(DefaultListableBeanFactory registry) {
        this.registry = registry;
    }


}
