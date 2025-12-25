package com.glee.xjpa.configuration;

import java.util.Set;

/**
 * @author GENSEN
 * @date 2022/11/14
 * @description 包数据源应用
 */
public interface RepositoryPackageDataSourceMapper {

    /**
     * 获取数据源名称
     * @param packageName
     * @return
     */
    String getDataSourceName(String packageName);

}
