package com.glee.xjpa.repository.impl;

import com.glee.xjpa.lifecycle.Closeable;
import com.glee.xjpa.lifecycle.Disposable;
import com.glee.xjpa.repository.StandardJpaRepository;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2022/11/8
 * @description 委托对象
 */
public interface RepositoryDelegateHolder<K extends Serializable, V> extends Disposable, Closeable {

    StandardJpaRepository<K, V> getDelegate(Object parent);

}
