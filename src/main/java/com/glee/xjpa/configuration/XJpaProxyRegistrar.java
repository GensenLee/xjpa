package com.glee.xjpa.configuration;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.annotation.XJpaRepository;
import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.datasource.LazyDataSourceManager;
import com.glee.xjpa.exception.XJpaInitException;
import com.glee.xjpa.io.StandardXJpaRepository;
import com.glee.xjpa.proxy.RepositoryFactoryBean;
import com.glee.xjpa.table.*;
import com.glee.xjpa.util.TableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * @author GENSEN
 * @date 2026/1/20
 * @description 启动引导
 */
public class XJpaProxyRegistrar implements ImportBeanDefinitionRegistrar, PriorityOrdered {

    private final static Logger log = LoggerFactory.getLogger(XJpaProxyRegistrar.class);


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        initXJpa((DefaultListableBeanFactory) registry, importBeanNameGenerator);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initXJpa(DefaultListableBeanFactory beanFactory, BeanNameGenerator importBeanNameGenerator) {
        log.info("XJpa starting...");

        XJpaProperties xJpaProperties = beanFactory.getBean(XJpaProperties.class);

        Map<String, XJpaDataSourceConfig> dataSourceConfigMap = beanFactory.getBeansOfType(XJpaDataSourceConfig.class);

        Map<String, TableProperties> tablePropertiesMap = new HashMap<>();

        XJpaResources xJpaResources = new DefaultXJpaResources(xJpaProperties, new ArrayList<>(dataSourceConfigMap.values()), tablePropertiesMap);

        beanFactory.registerSingleton("xJpaResources", xJpaResources);

        DataSourceManager dataSourceManager = new LazyDataSourceManager(xJpaResources, beanFactory);

        TableInitErrorHandler initErrorHandler = new DefaultTableInitErrorHandler(dataSourceManager);

        log.trace("found {} data source configs", dataSourceConfigMap.size());

        RepositoryScannerResult scannerResult = scanXjpaRepositoryOfPackages(dataSourceConfigMap.values());

        try {
            List<TableFieldProvider> tableFieldProviderList = new ArrayList<>();

            Set<String> classes = scannerResult.getAllScannedRepositoryClasses();
            for (String repositoryClassName : classes) {
                Class<?> repositoryClass = ClassUtils.forName(repositoryClassName, StandardXJpaRepository.class.getClassLoader());
                String dataSourceName = scannerResult.getDataSourceName(repositoryClass, repositoryClass.getPackageName());

                TableFieldProvider container = new LazyLoadTableFieldProvider(repositoryClass, initErrorHandler, dataSourceManager);
                tableFieldProviderList.add(container);
                XJpaTableMetadata metadata = TableUtil.createMetadata(repositoryClass, container);

                TableProperties tableProperties = new TableProperties(repositoryClass, metadata, dataSourceName);

                tablePropertiesMap.put(metadata.getTableName(), tableProperties);

                log.trace("creating proxy for {}", repositoryClass);

                AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(RepositoryFactoryBean.class)
                        .addConstructorArgValue(repositoryClass)
                        .addConstructorArgValue(tableProperties)
                        .addConstructorArgValue(dataSourceManager)
                        .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                        .setScope(BeanDefinition.SCOPE_SINGLETON)
                        .getBeanDefinition();

                String beanName = importBeanNameGenerator.generateBeanName(
                        BeanDefinitionBuilder.genericBeanDefinition(repositoryClass).getBeanDefinition(), beanFactory);

                // 注册到Spring容器
                beanFactory.registerBeanDefinition(beanName, beanDefinition);
            }

            // 配置启动完成后自动加载表配置
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(TableFieldsLoader.class)
                    .addConstructorArgValue(tableFieldProviderList)
                    .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO)
                    .getBeanDefinition();

            String beanName = importBeanNameGenerator.generateBeanName(
                    BeanDefinitionBuilder.genericBeanDefinition(TableFieldsLoader.class).getBeanDefinition(), beanFactory);

            beanFactory.registerBeanDefinition(beanName, beanDefinition);

        } catch (Exception e) {
            log.error("XJpa init error", e);
            throw new XJpaInitException("XJpa init error", e);
        }
    }

    /**
     * 扫描
     * @param dataSourceConfigs
     * @return
     */
    private RepositoryScannerResult scanXjpaRepositoryOfPackages(Collection<XJpaDataSourceConfig> dataSourceConfigs) {
        // 将数据源配置去重，保证一个包路径仅匹配一个数据源
        // 包的长路径可以覆盖短路径的配置
        // 比如 com.glee.rep.user->datasource2 配置会覆盖 com.glee.rep->datasource1 配置
        // 如果同一个包路径出现多个数据源配置，仅保留其中一个
        // 仅保留继承自

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false){
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                AnnotationMetadata metadata = beanDefinition.getMetadata();
                return (metadata.isIndependent() ||
                        (metadata.isAbstract() && metadata.hasAnnotatedMethods(Lookup.class.getName())));
            }
        };

        scanner.addIncludeFilter(new AnnotationTypeFilter(XJpaRepository.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(StandardXJpaRepository.class));
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            // 检查是否是 StandardXJpaRepository 的子接口
            String className = metadataReader.getClassMetadata().getClassName();
            try {
                Class<?> clazz = Class.forName(className);
                // 包含接口和具体类
                return StandardXJpaRepository.class.isAssignableFrom(clazz);
            } catch (ClassNotFoundException e) {
                return false;
            }
        });

        RepositoryScannerResult scannerResult = new RepositoryScannerResult();

        for (XJpaDataSourceConfig dataSourceConfig : dataSourceConfigs) {
            Map<String, String> packageDataSourceMapping = dataSourceConfig.getPackageDataSourceMapping();
            for (Map.Entry<String, String> entry : packageDataSourceMapping.entrySet()) {
                String dataSourceName = entry.getValue();
                String packageName = entry.getKey();
                log.trace("scanning package {}", packageName);
                if (ArrayUtil.isEmpty(packageName) || StrUtil.isEmpty(dataSourceName)) {
                    continue;
                }
                Set<BeanDefinition> repositoryDefinitions = scanner.findCandidateComponents(packageName);
                log.trace("{} components found in package {}", repositoryDefinitions.size(), packageName);
                for (BeanDefinition repositoryDefinition : repositoryDefinitions) {
                    scannerResult.addRepositoryClassName(packageName, dataSourceName, repositoryDefinition.getBeanClassName());
                }
            }

        }

        return scannerResult;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
