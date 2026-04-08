package com.glee.xjpa.configuration;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.exception.XJpaInitException;

import java.util.*;

/**
 * @author GENSEN
 * @date 2026/1/20
 * @description 扫描过程工具
 */
public class RepositoryScannerResult {

    private final Map<String, List<String>> packageRepositoryClassNameMapping = new HashMap<>();

    private final Map<String, String> packageDataSourceNameMapping = new HashMap<>();

    private final Map<String, String> repositoryDataSourceMapping = new HashMap<>();

    public void addRepositoryClassName(String packageName, String dataSourceName, String className) {

        packageDataSourceNameMapping.put(packageName, dataSourceName);
        List<String> reps = packageRepositoryClassNameMapping.getOrDefault(packageName, new ArrayList<>());
        reps.add(className);
        packageRepositoryClassNameMapping.put(packageName, reps);

        repositoryDataSourceMapping.put(className, dataSourceName);
    }


    public Map<String, List<String>> getAllScannedRepositoryClassesMapping() {
        return packageRepositoryClassNameMapping;
    }

    public String getDataSourceName(Class<?> repositoryClass, String packageName) {
        if (StrUtil.isEmpty(packageName)) {
            throw new XJpaInitException("data source not found for %s".formatted(repositoryClass));
        }
        String dataSourceName = packageDataSourceNameMapping.get(packageName);
        if (StrUtil.isNotEmpty(dataSourceName)) {
            return dataSourceName;
        }
        String parentPackageName = packageName.substring(0, packageName.lastIndexOf("."));
        return getDataSourceName(repositoryClass, parentPackageName);
    }

    public Set<String> getAllScannedRepositoryClasses() {
        return repositoryDataSourceMapping.keySet();
    }
}
