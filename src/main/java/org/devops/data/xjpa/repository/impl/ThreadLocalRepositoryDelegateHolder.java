package org.devops.data.xjpa.repository.impl;

import org.devops.data.xjpa.lifecycle.Closeable;
import org.devops.data.xjpa.lifecycle.Disposable;
import org.devops.data.xjpa.repository.StandardJpaRepository;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2022/11/8
 * @description 线程隔离模式委托
 */
public class ThreadLocalRepositoryDelegateHolder<K extends Serializable, V> implements RepositoryDelegateHolder<K, V> {

    private final RepositoryProxyBeanFactory<StandardJpaRepository<K, V>> modelRepositoryFactory;

    private final Class<?> repositoryType;

    private final ThreadLocal<StandardJpaRepository<K, V>> repositoryThreadLocal = new ThreadLocal<>();

    public ThreadLocalRepositoryDelegateHolder(RepositoryProxyBeanFactory<StandardJpaRepository<K, V>> repositoryFactory,
                                               Class<?> repositoryType) {
        this.modelRepositoryFactory = repositoryFactory;
        this.repositoryType = repositoryType;
    }


    @Override
    public StandardJpaRepository<K, V> getDelegate(Object parent) {
        StandardJpaRepository<K, V> standardJpaRepository = repositoryThreadLocal.get();
        // 这里存在并发问题
        // 如果多个接口并发,多个接口都涉及到同一张表的初始化,就会初始化了两次,会导致后续逻辑异常
        // 已出现过的异常: 逻辑删除问题,报错表不存在逻辑删除字段,因为并发导致线程1 load 完成并标记 loaded 而方法为返回,
        // 但是线程2已经得到 loaded 并读到空的加载结果
        if (standardJpaRepository == null) {
            synchronized(repositoryThreadLocal) {
                standardJpaRepository = repositoryThreadLocal.get();
                if (standardJpaRepository == null) {
                    repositoryThreadLocal.set(doCreate(parent));
                }
            }
            standardJpaRepository = repositoryThreadLocal.get();
        }

        return standardJpaRepository;
    }

    /**
     * @param parent
     * @return
     */
    private StandardJpaRepository<K, V> doCreate(Object parent) {
        StandardJpaRepository<K, V> standardJpaRepository = modelRepositoryFactory.getProxy(repositoryType);
        if (standardJpaRepository instanceof RepositoryContextObservable && parent instanceof RepositoryContextObserver) {
            ((RepositoryContextObservable) standardJpaRepository).register((RepositoryContextObserver) parent);
        }

        return standardJpaRepository;
    }

    @Override
    public void close() {
        StandardJpaRepository<K, V> repository = repositoryThreadLocal.get();
        repositoryThreadLocal.remove();
        if (repository instanceof Closeable) {
            ((Closeable) repository).close();
        }
    }

    @Override
    public void dispose() {
        StandardJpaRepository<K, V> repository = repositoryThreadLocal.get();
        repositoryThreadLocal.remove();
        if (repository instanceof Disposable) {
            ((Disposable) repository).dispose();
        }
    }
}
