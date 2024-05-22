package org.devops.data.xjpa.repository.impl;

/**
 * @author GENSEN
 * @date 2022/11/9
 * @description context观察者
 */
@SuppressWarnings({"rawtypes"})
public interface RepositoryContextObserver {

    /**
     * @param context
     */
    void onDispose(RepositoryContext context);

    /**
     * @param context
     */
    void onClose(RepositoryContext context);

}
