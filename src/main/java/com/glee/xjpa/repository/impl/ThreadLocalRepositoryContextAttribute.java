package com.glee.xjpa.repository.impl;

import com.glee.xjpa.lifecycle.Closeable;
import com.glee.xjpa.lifecycle.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GENSEN
 * @date 2022/11/9
 * @description 基于threadLocal属性管理
 */
public class ThreadLocalRepositoryContextAttribute implements RepositoryContextAttribute, RepositoryContextObserver {

    protected static final Logger logger = LoggerFactory.getLogger(ThreadLocalRepositoryContextAttribute.class);


    private final Map<String, Object> attributes;

    public ThreadLocalRepositoryContextAttribute() {
        this.attributes = new ConcurrentHashMap<>();
    }

    @Override
    public void close() {
        for (Object value : attributes.values()) {
            if (value instanceof Closeable) {
                ((Closeable) value).close();
            }
        }
        attributes.clear();
    }

    @Override
    public void dispose() {
        logger.trace("{} dispose", this.getClass());

        for (Object value : attributes.values()) {
            if (value instanceof Disposable) {
                ((Disposable) value).dispose();
            }
        }
//        attributes.clear();
    }

    @Override
    public void setAttribute(String key, Object attribute) {
        attributes.put(key, attribute);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSingleton(Class<T> beanType) {
        return (T) attributes.get(beanType.getName());
    }

    @Override
    public Set<String> keySet() {
        return attributes.keySet();
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public void onDispose(RepositoryContext context) {
        dispose();
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public void onClose(RepositoryContext context) {
        close();
    }
}
