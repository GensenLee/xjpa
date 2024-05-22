package org.devops.data.xjpa.configuration.configsource;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description 数据源扫描
 */
@Deprecated
public interface XjpaDataSourceScanner {

    /**
     * 包扫描
     * @return package -> dataSourceName
     */
    Map<String, String> scan(DefaultListableBeanFactory beanFactory);

}
