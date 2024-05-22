package org.devops.data.xjpa.repository.impl;

import org.devops.data.xjpa.lifecycle.Closeable;
import org.devops.data.xjpa.lifecycle.Disposable;
import org.devops.data.xjpa.repository.StandardJpaRepository;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2022/11/8
 * @description 委托对象
 */
public interface RepositoryDelegateHolder<K extends Serializable, V> extends Disposable, Closeable {

    StandardJpaRepository<K, V> getDelegate(Object parent);

}
