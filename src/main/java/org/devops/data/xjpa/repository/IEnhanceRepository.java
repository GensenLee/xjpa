package org.devops.data.xjpa.repository;

import org.devops.data.xjpa.sql.executor.SortType;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2022/10/28
 * @description 增强
 */
public interface IEnhanceRepository<K extends Serializable, V> extends IXjpaRepository<K, V> {


    IEnhanceRepository<K, V> ignoreSoftDelete();

    IEnhanceRepository<K, V> groupByColumns(String... columns);

    IEnhanceRepository<K, V> having(String havingString);

    IEnhanceRepository<K, V> distinct(String... columns);


    IEnhanceRepository<K, V> include(String... columns);

    IEnhanceRepository<K, V> exclude(String... columns);

    IEnhanceRepository<K, V> limit(int size);

    IEnhanceRepository<K, V> limit(int start, int size);

    IEnhanceRepository<K, V> limit(boolean isPage, int start, int size);


    //<editor-fold desc="排序">

    /**
     * 指定排序语句
     *
     * @param orderByString 如 `time desc`
     * @return
     */
    IEnhanceRepository<K, V> orderString(String orderByString);

    IEnhanceRepository<K, V> orderByColumn(String column, SortType sortType);

    default IEnhanceRepository<K, V> descByColumn(String column) {
        return orderByColumn(column, SortType.desc);
    }

    default IEnhanceRepository<K, V> ascByColumn(String column) {
        return orderByColumn(column, SortType.asc);
    }
    //</editor-fold>
}
