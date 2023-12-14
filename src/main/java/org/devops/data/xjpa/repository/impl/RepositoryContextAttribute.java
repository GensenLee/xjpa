package org.devops.data.xjpa.repository.impl;

import org.devops.data.xjpa.lifecycle.Closeable;
import org.devops.data.xjpa.lifecycle.Disposable;

import java.util.Set;

/**
 * @author GENSEN
 * @date 2022/11/9
 * @description 属性管理
 */
public interface RepositoryContextAttribute extends Disposable, Closeable {

    void setAttribute(String key, Object attribute);

    Object getAttribute(String key);

    <T> T getSingleton(Class<T> beanType);

    Set<String> keySet();

}
