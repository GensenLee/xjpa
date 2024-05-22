package org.devops.data.xjpa.repository;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author GENSEN
 * @date 2022/10/28
 * @description 插入
 */
public interface IInsertRepository<K extends Serializable, V> extends IXjpaRepository<K, V> {


    int insert(V entities);


    int insert(Collection<V> entity);
}
