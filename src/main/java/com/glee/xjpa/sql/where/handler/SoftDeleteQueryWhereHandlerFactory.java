package com.glee.xjpa.sql.where.handler;

import com.glee.xjpa.repository.impl.RepositoryContext;

/**
 * @author GENSEN
 * @date 2022/11/22
 * @description 逻辑删除支持
 */
@SuppressWarnings("rawtypes")
public class SoftDeleteQueryWhereHandlerFactory implements QueryWhereHandlerFactory{

    private final RepositoryContext context;

    public SoftDeleteQueryWhereHandlerFactory(RepositoryContext context) {
        this.context = context;
    }

    @Override
    public IQueryWhereHandler getHandler() {
        return new SoftDeleteQueryWhereHandler(context);
    }
}
