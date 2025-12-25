package com.glee.xjpa.repository.impl;

import com.glee.xjpa.sql.where.handler.IQueryWhereHandler;
import com.glee.xjpa.sql.where.handler.QueryWhereHandlerFactory;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description context 类型
 */
public abstract class RepositoryContextBean<K, V> implements ContextSupplier {

    protected final RepositoryContext<K, V> context;

    public RepositoryContextBean(RepositoryContext<K, V> context) {
        this.context = context;
    }

    @Override
    public RepositoryContext<K, V> getContext() {
        return context;
    }

    protected IQueryWhereHandler getWhereHandler() {

        QueryWhereHandlerFactory handlerFactory = context.getSingleton(QueryWhereHandlerFactory.class);

        return handlerFactory.getHandler();
    }


}
