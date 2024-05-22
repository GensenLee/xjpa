package org.devops.data.xjpa.configuration.configsource;

import cn.hutool.core.util.StrUtil;
import org.devops.data.xjpa.annotation.XjpaDataSource;
import org.devops.data.xjpa.exception.XjpaInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description 注解方式数据源扫描 XjpaDataSource
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
// 该类会导致数据源配置丢失，得到一个空的数据源对象，没有url、username、password等。
// 原因未明
public class AnnotationXjpaDataSourceScanner implements XjpaDataSourceScanner {
    protected static final Logger logger = LoggerFactory.getLogger(AnnotationXjpaDataSourceScanner.class);


    @Override
    public Map<String, String> scan(DefaultListableBeanFactory beanFactory) {
        String[] beanNamesForAnnotation = beanFactory.getBeanNamesForAnnotation(XjpaDataSource.class);

        // 过早获取实例或导致bean无法增强，例如dataSource没有设置到url等属性
        //        Map<String, Object> modelRepositoryDataSources = beanFactory.getBeansWithAnnotation(XjpaDataSource.class);

        List<String> configDataSourceNameList = Arrays.stream(beanNamesForAnnotation)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(configDataSourceNameList)) {
            return Collections.emptyMap();
            // 仅兼容，不报错
//            throw new XjpaInitException("请配置数据源 @XjpaDataSource(dataSourceForPackage=xxx)");
        }

        // 值定义
        return getConfigPackageAndDatasource(configDataSourceNameList, beanFactory);
    }


    /**
     * 获取配置数据源
     *
     * @param configDataSourceNameList
     * @param beanFactory
     * @return package -> datasourceName
     */
    private Map<String, String> getConfigPackageAndDatasource(List<String> configDataSourceNameList, DefaultListableBeanFactory beanFactory) {

        Map<String, XjpaDataSource> datasourceAnnotationMap = new HashMap<>();
        for (String dataSourceName : configDataSourceNameList) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(dataSourceName);
            if (StrUtil.isEmpty(beanDefinition.getFactoryBeanName())) {
                logger.error("datasource bind error, beanDefinition.getFactoryBeanName() = {}", beanDefinition.getFactoryBeanName());
                throw new XjpaInitException("datasource bind error");
            }
            try {
                Object factoryBean = beanFactory.getBean(beanDefinition.getFactoryBeanName());

                Class<?> factoryType = null;
                if (Enhancer.isEnhanced(factoryBean.getClass())) {
                    factoryType = factoryBean.getClass().getSuperclass();
                }else {
                    factoryType = factoryBean.getClass();
                }

                Method datasourceDeclaredMethod = Arrays.stream(factoryType.getDeclaredMethods())
                        .filter(method -> method.getName().equals(beanDefinition.getFactoryMethodName()))
                        .filter(method -> method.isAnnotationPresent(XjpaDataSource.class))
                        .findFirst()
                        .orElse(null);
                if (datasourceDeclaredMethod == null) {
                    logger.error("datasource bind error, datasourceDeclaredMethod == null, factoryClass={}, beanDefinition.getFactoryMethodName()={}",
                            factoryType, beanDefinition.getFactoryMethodName());
                    throw new XjpaInitException("datasource bind error");
                }

                if (!datasourceDeclaredMethod.getReturnType().isAssignableFrom(DataSource.class)) {
                    // 非数据源配置
                    continue;
                }

                XjpaDataSource xjpaDataSource = AnnotationUtils.findAnnotation(datasourceDeclaredMethod, XjpaDataSource.class);
                datasourceAnnotationMap.put(dataSourceName, xjpaDataSource);
            } catch (Exception e) {
                throw new XjpaInitException(e);
            }
        }

        ensureDatasourceMappingValid(datasourceAnnotationMap);

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, XjpaDataSource> entry : datasourceAnnotationMap.entrySet()) {
            final String datasourceName = entry.getKey();
            Map<String, String> config = Arrays.stream(entry.getValue().dataSourceForPackages())
                    .collect(Collectors.toMap(Function.identity(), pkg -> datasourceName));
            result.putAll(config);
        }
        return result;
    }

    /**
     * 确保数据映射关系正确
     *
     * @param datasourceAnnotationMap
     */
    private void ensureDatasourceMappingValid(Map<String, XjpaDataSource> datasourceAnnotationMap) {
        Map<String, Long> counting = datasourceAnnotationMap.values().stream()
                .map(XjpaDataSource::dataSourceForPackages)
                .flatMap(Arrays::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        for (Map.Entry<String, Long> entry : counting.entrySet()) {
            if (entry.getValue() > 1) {
                throw new XjpaInitException("multiple datasource config found, package = " + entry.getKey());
            }
        }
    }

}
