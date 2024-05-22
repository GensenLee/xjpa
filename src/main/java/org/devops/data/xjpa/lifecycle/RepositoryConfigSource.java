package org.devops.data.xjpa.lifecycle;

import org.devops.data.xjpa.configuration.RepositoriesConfigurationManager;
import org.devops.data.xjpa.configuration.RepositoryGlobalConfig;
import org.devops.data.xjpa.configuration.Refreshable;
import org.springframework.core.env.Environment;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description 配置源构造
 */
public interface RepositoryConfigSource extends Refreshable {

    RepositoryGlobalConfig getGlobalConfig();

    RepositoriesConfigurationManager getConfigManager();

    Environment getEnvironment();


}
