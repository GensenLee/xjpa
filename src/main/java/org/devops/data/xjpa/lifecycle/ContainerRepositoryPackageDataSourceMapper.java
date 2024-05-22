package org.devops.data.xjpa.lifecycle;

import cn.hutool.core.util.StrUtil;
import org.devops.data.xjpa.configuration.RepositoryPackageDataSourceMapper;
import org.devops.data.xjpa.constant.XjpaConstant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/14
 * @description 懒加载
 */
public class ContainerRepositoryPackageDataSourceMapper implements RepositoryPackageDataSourceMapper {

    public static String DEFAULT_DATASOURCE_NAME = "";

    private final Map<String, String> repositoryPackageDataSourceMapping;

    public ContainerRepositoryPackageDataSourceMapper(Map<String[], String> repositoryPackageDataSource) {
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
