package org.devops.data.xjpa.configuration;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description model配置管理器
 */
public interface RepositoriesConfigurationManager extends Refreshable {


    RepositoryProperties getRepositoryProperties(Class repositoryType);

    RepositoryGlobalConfig getGlobalConfig();

}
