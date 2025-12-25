package com.glee.xjpa.repository.impl;

/**
 * @author GENSEN
 * @date 2022/11/9
 * @description context变动观察
 */
public interface RepositoryContextObservable {

    /**
     * @param observer
     */
    void register(RepositoryContextObserver observer);

    void notifyDispose();

    void notifyClose();

}
