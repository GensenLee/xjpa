package com.glee.xjpa.configuration.configsource;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/27
 * @description bean factory
 */
public class AnnotationDataSourceBeanConfig implements XjpaRepositoryBeanConfig{


    private final AnnotationXjpaDataSourceScanner annotationXjpaDataSourceScanner;

    public AnnotationDataSourceBeanConfig(DefaultListableBeanFactory beanFactory) {
        this.annotationXjpaDataSourceScanner = new AnnotationXjpaDataSourceScanner();
        this.beanFactory = beanFactory;
    }

    private final DefaultListableBeanFactory beanFactory;

    @Override
    public Map<String[], String> scanPackages() {
        Map<String, String> scanResult = annotationXjpaDataSourceScanner.scan(beanFactory);

        return scanResult.entrySet().stream()
                // 按数据源名称分组
                .collect(Collectors.groupingBy(Map.Entry::getValue))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e ->
                                e.getValue().stream()
                                        .map(Map.Entry::getKey).toList()
                                        .toArray(new String[]{}),
                        Map.Entry::getKey));
    }
}
