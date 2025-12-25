package com.glee.xjpa.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/10/28
 * @description 查询
 */
public interface ISelectRepository<K extends Serializable, V> extends IXjpaRepository<K, V> {

    List<V> list();

    List<V> listByIds(Collection<K> keys);

    <T> List<T> list(Class<T> resultType);

    V get();

    V getById(K key);

    <T> T get(Class<T> resultType);

    boolean isExists();

    boolean isExistsById(K key);

    long count();

}
