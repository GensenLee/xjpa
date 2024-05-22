package org.devops.data.xjpa.sql.where.handler;

import org.devops.data.xjpa.repository.impl.RepositoryContext;

/**
 * @author GENSEN
 * @date 2022/11/22
 * @description 默认
 */
@SuppressWarnings("rawtypes")
public class DefaultQueryWhereHandlerFactory implements QueryWhereHandlerFactory{

    private final RepositoryContext context;

    public DefaultQueryWhereHandlerFactory(RepositoryContext context) {
        this.context = context;
    }

    @Override
    public IQueryWhereHandler getHandler() {
        return new DefaultQueryWhereHandler(context.localQueryWhere().combine(context));
    }
}
