package org.devops.data.xjpa.configuration.configsource;

import cn.hutool.core.util.StrUtil;
import org.devops.data.xjpa.exception.XjpaInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/26
 * @description 全路径扫描
 */
public class ClasspathRepositoryBeanFactory implements RepositoryBeanFactory {
    protected static final Logger logger = LoggerFactory.getLogger(ClasspathRepositoryBeanFactory.class);


    private final DefaultListableBeanFactory beanFactory;

    private final String[] basePackages;

    public ClasspathRepositoryBeanFactory(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.basePackages = findBasePackage(beanFactory);
    }

    /**
     * @param beanFactory
     * @return
     */
    private String[] findBasePackage(DefaultListableBeanFactory beanFactory) {

        String[] componentScanBeanNames = beanFactory.getBeanNamesForAnnotation(ComponentScan.class);
        if (componentScanBeanNames.length == 0) {
            return new String[0];
        }

        Set<String> scanResultSet = new HashSet<>();


        for (String componentBeanName : componentScanBeanNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(componentBeanName);

            Class<?> beanType = beanDefinition.getResolvableType().resolve();
            if (beanType == null) {
                if (StrUtil.isNotEmpty(beanDefinition.getBeanClassName())) {
                    beanType = ClassUtils.resolveClassName(beanDefinition.getBeanClassName(), null);
                }else if (StrUtil.isNotEmpty(beanDefinition.getFactoryBeanName())){
                    beanType = ClassUtils.resolveClassName(beanDefinition.getFactoryBeanName(), null);
                }else {
                    logger.error("can not find bean class, beanName {}", componentBeanName);
                    continue;
                }
            }


            AnnotationMetadata annotationMetadata = AnnotationMetadata.introspect(beanType);
            MergedAnnotation<ComponentScan> componentScanMergedAnnotation = annotationMetadata.getAnnotations().get(ComponentScan.class);
            if (!componentScanMergedAnnotation.isPresent()) {
                continue;
            }
            scanResultSet.add(ClassUtils.getPackageName(componentBeanName.getClass()));
            ComponentScan componentScan = componentScanMergedAnnotation.synthesize();
            Set<String> packages = Arrays.stream(componentScan.basePackageClasses())
                    .map(ClassUtils::getPackageName)
                    .collect(Collectors.toSet());
            scanResultSet.addAll(packages);

            if (componentScan.basePackages().length > 0) {
                scanResultSet.addAll(Arrays.asList(componentScan.value()));
            }

        }

        return scanResultSet.toArray(new String[0]);
    }


    @Override
    public String[] baseScanPackages() {
        return basePackages;
    }

    @Override
    public String dataSourceName() {
        String[] beanNamesForType = beanFactory.getBeanNamesForType(DataSource.class);
        if (beanNamesForType.length == 0) {
            throw new XjpaInitException("DataSource not found");
        }
        return beanNamesForType[0];
    }
}
