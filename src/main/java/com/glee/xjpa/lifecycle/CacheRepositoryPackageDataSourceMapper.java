package com.glee.xjpa.lifecycle;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.configuration.RepositoryPackageDataSourceMapper;
import com.glee.xjpa.constant.XjpaConstant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/14
 * @description 懒加载
 */
public class CacheRepositoryPackageDataSourceMapper implements RepositoryPackageDataSourceMapper {

    public static String DEFAULT_DATASOURCE_NAME = "";

    private final Map<String, String> repositoryPackageDataSourceMapping;

    public CacheRepositoryPackageDataSourceMapper(Map<String[], String> repositoryPackageDataSource) {
        this.repositoryPackageDataSourceMapping = new HashMap<>();

        repositoryPackageDataSource.forEach((packages, dataSourceName) ->
                        Arrays.stream(packages).forEach(pkg -> repositoryPackageDataSourceMapping.put(pkg, dataSourceName))
                );
    }

    @Override
    public String getDataSourceName(String packageName) {
        if (StrUtil.isEmpty(packageName)) {
            return DEFAULT_DATASOURCE_NAME;
        }
        if (!packageName.contains(XjpaConstant.POINT_MARK)) {
            return getDataSourceName(null);
        }

        String dataSourceName = repositoryPackageDataSourceMapping.get(packageName);

        if (StrUtil.isNotEmpty(dataSourceName)) {
            return dataSourceName;
        }

        int endIndex = packageName.lastIndexOf(XjpaConstant.POINT_MARK);
        return getDataSourceName(packageName.substring(0, endIndex));
    }
}
