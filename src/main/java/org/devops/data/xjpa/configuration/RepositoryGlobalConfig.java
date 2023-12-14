package org.devops.data.xjpa.configuration;

import org.devops.data.xjpa.repository.StandardJpaRepository;

import java.util.Set;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description 全局配置
 */
@SuppressWarnings("rawtypes")
public interface RepositoryGlobalConfig extends Refreshable{

    String PREFIX = "devops.data.xjpa.";

    /**
     * ture or false
     */
    String SOFT_DELETE_ENABLED = PREFIX + "soft-delete.enabled";

    /**
     * 逻辑删除控制列
     */
    String SOFT_DELETE_COLUMN = PREFIX + "soft-delete.column";

    /**
     * 未删除的值
     */
    String SOFT_DELETE_NOT_DELETE_VALUE = PREFIX + "soft-delete.not-delete-value";

    /**
     * 删除的值
     */
    String SOFT_DELETE_DELETED_VALUE = PREFIX + "soft-delete.deleted-value";


    /**
     * @return 所有扫描包
     */
    Set<String> baseRepositoryPackages();

    /**
     * 获取所有 Repository 类型
     * @return
     */
    Set<Class<? extends StandardJpaRepository>> getRepositoryTypes();


    /**
     * 获取包数据源名称
     * @param packageName
     * @return
     */
    String getDataSourceName(String packageName);

    /**
     * 获取环境配置
     * @param key
     * @return
     */
    String getProperty(String key);
}
