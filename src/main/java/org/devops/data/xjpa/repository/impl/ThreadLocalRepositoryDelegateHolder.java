package org.devops.data.xjpa.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.devops.data.xjpa.lifecycle.Closeable;
import org.devops.data.xjpa.lifecycle.Disposable;
import org.devops.data.xjpa.repository.StandardJpaRepository;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2022/11/8
 * @description 线程隔离模式委托
 */
@Slf4j
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
        if (standardJpaRepository == null) {
            if (repositoryThreadLocal.get() == null) {
                repositoryThreadLocal.set(doCreate(parent));
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
