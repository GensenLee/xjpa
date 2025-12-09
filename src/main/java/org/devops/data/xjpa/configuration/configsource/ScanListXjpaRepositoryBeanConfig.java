package org.devops.data.xjpa.configuration.configsource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/15
 * @description 扫描配置列表
 */
public class ScanListXjpaRepositoryBeanConfig implements XjpaRepositoryBeanConfig {

    protected static final Logger logger = LoggerFactory.getLogger(ScanListXjpaRepositoryBeanConfig.class);

    private final DefaultListableBeanFactory beanFactory;

    public ScanListXjpaRepositoryBeanConfig(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    public Map<String[], String> scanPackages() {

        Map<String[], String> result = new HashMap<>();

        Map<String, RepositoryBeanFactory> beansOfType = beanFactory.getBeansOfType(RepositoryBeanFactory.class);

        // 依赖组件中可能会存在 RepositoryBeanFactory ，所以默认都需要添加这个扫描器
        beansOfType.put("classpathRepositoryBeanFactory", new ClasspathRepositoryBeanFactory(beanFactory));

        beansOfType.values().forEach(repositoryBeanFactory -> {
            result.put(repositoryBeanFactory.baseScanPackages(), repositoryBeanFactory.dataSourceName());
            logger.trace("config datasource '{}' on package [{}]", repositoryBeanFactory.dataSourceName(), repositoryBeanFactory.baseScanPackages());
        });
        return result;
    }
}
