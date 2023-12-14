package org.devops.data.xjpa.configuration;

import cn.hutool.core.util.StrUtil;
import org.devops.data.xjpa.configuration.configsource.AnnotationDataSourceBeanConfig;
import org.devops.data.xjpa.configuration.configsource.ScanListXjpaRepositoryBeanConfig;
import org.devops.data.xjpa.configuration.configsource.XjpaRepositoryBeanConfig;
import org.devops.data.xjpa.exception.XjpaException;
import org.devops.data.xjpa.lifecycle.ContainerRepositoryPackageDataSourceMapper;
import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.devops.data.xjpa.util.ReflectionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description 基于spring 支持的配置获取方式
 */
@SuppressWarnings("rawtypes")
public class SpringRepositoryGlobalConfig implements RepositoryGlobalConfig {

    protected static final Logger logger = LoggerFactory.getLogger(SpringRepositoryGlobalConfig.class);


    private final DefaultListableBeanFactory beanFactory;
    private final Environment environment;
    private RepositoryPackageDataSourceMapper repositoryPackageDataSourceMapper;
    private final List<XjpaRepositoryBeanConfig> xjpaRepositoryBeanConfigList = new ArrayList<>();

    /**
     * pkg -> dataSourceName
     */
    private final Map<String[], String> packageDataSourceMapping = new HashMap<>();

    public SpringRepositoryGlobalConfig(DefaultListableBeanFactory beanFactory, Environment environment) {
        this.beanFactory = beanFactory;
        this.environment = environment;
    }

    private void addDefaultBeanConfig(DefaultListableBeanFactory beanFactory, Environment environment) {
        xjpaRepositoryBeanConfigList.add(new ScanListXjpaRepositoryBeanConfig(beanFactory));
        xjpaRepositoryBeanConfigList.add(new AnnotationDataSourceBeanConfig(beanFactory));
    }


    @Override
    public Set<String> baseRepositoryPackages() {
        return packageDataSourceMapping.keySet().stream()
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Class<? extends StandardJpaRepository>> getRepositoryTypes() {
        Set<Class<? extends StandardJpaRepository>> result = new HashSet<>();
        for (String baseRepositoryPackage : baseRepositoryPackages()) {
            Set<Class<? extends StandardJpaRepository>> subTypesOfModelRepository =
                    ReflectionsUtil.getSubTypesOfStandardJpaRepository(baseRepositoryPackage);
            result.addAll(subTypesOfModelRepository);
        }
        return result;
    }

    @Override
    public String getDataSourceName(String packageName) {
        String dataSourceName = repositoryPackageDataSourceMapper.getDataSourceName(packageName);
        if (StrUtil.isNotEmpty(dataSourceName)) {
            return dataSourceName;
        }

        // 没有配置包数据源时，使用第一个数据源
        String[] beanNamesForType = beanFactory.getBeanNamesForType(DataSource.class);
        if (beanNamesForType.length > 0) {
            return beanNamesForType[0];
        }

        throw new XjpaException("datasource not found");
    }

    @Override
    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    @Override
    public void refresh() {

        xjpaRepositoryBeanConfigList.clear();
        addDefaultBeanConfig(beanFactory, environment);
        ObjectProvider<XjpaRepositoryBeanConfig> beanProvider = beanFactory.getBeanProvider(XjpaRepositoryBeanConfig.class);
        xjpaRepositoryBeanConfigList.addAll(beanProvider.stream().collect(Collectors.toList()));

        List<Map<String[], String>> scanResultList = xjpaRepositoryBeanConfigList.stream()
                .map(XjpaRepositoryBeanConfig::scanPackages)
                .collect(Collectors.toList());


        for (Map<String[], String> scanResult : scanResultList) {
            packageDataSourceMapping.putAll(scanResult);
        }

        repositoryPackageDataSourceMapper = new ContainerRepositoryPackageDataSourceMapper(packageDataSourceMapping);
    }
}
