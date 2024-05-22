package org.devops.data.xjpa.repository;

import java.io.Serializable;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/10/28
 * @description 单列查询支持
 */
public interface ISingleColumnSupportSelectRepository<K extends Serializable, V> extends IXjpaRepository<K, V> {

    <T> List<T> listSingleColumn(Class<T> clazz);

    <T> T getSingleColumn(Class<T> clazz);

    default String getStringValue(){
        return getSingleColumn(String.class);
    }

    default Integer getIntegerValue(){
        return getSingleColumn(Integer.class);
    }

    default Long getLongValue(){
        return getSingleColumn(Long.class);
    }

    default List<String> listStringValue(){
        return listSingleColumn(String.class);
    }

    default List<Integer> listIntegerValue(){
        return listSingleColumn(Integer.class);
    }

    default List<Long> listLongValue(){
        return listSingleColumn(Long.class);
    }

}
