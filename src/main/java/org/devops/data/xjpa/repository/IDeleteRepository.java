package org.devops.data.xjpa.repository;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author GENSEN
 * @date 2022/10/28
 * @description 删除
 */
public interface IDeleteRepository<K extends Serializable, V> extends IXjpaRepository<K, V> {

    int deleteById(K key);

    int deleteByIds(Collection<K> keys);

    int delete(Collection<V> entities);

    int delete();

}
