package org.devops.data.xjpa.repository.impl;

import org.devops.data.xjpa.sql.where.handler.IQueryWhereHandler;
import org.devops.data.xjpa.sql.where.handler.QueryWhereHandlerFactory;

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
