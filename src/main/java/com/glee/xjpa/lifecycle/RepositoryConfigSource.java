package com.glee.xjpa.lifecycle;

import com.glee.xjpa.configuration.Refreshable;
import com.glee.xjpa.configuration.RepositoriesConfigurationManager;
import com.glee.xjpa.configuration.RepositoryGlobalConfig;
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
