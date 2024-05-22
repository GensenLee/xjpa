package org.devops.data.xjpa.sql.executor.query;

import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.sql.where.handler.IQueryWhereHandler;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 查询
 */
public abstract class AbstractQueryRequest<K, V> {

    /**
     *
     */
    final RepositoryContext<K, V> context;


    /**
     *
     */
    protected IQueryWhereHandler queryWhereHandler;


    public AbstractQueryRequest(RepositoryContext<K, V> context) {
        this.context = context;
    }

    public boolean emptyWhere() {
        if (queryWhereHandler != null) {
            return queryWhereHandler.isEmpty();
        }

        return context.localQueryWhere() == null || context.localQueryWhere().isEmpty();
    }


    public EntityTable<K, V> getEntityTable() {
        return context.getEntityTable();
    }

    public RepositoryContext<K, V> getContext() {
        return context;
    }

    public IQueryWhereHandler getQueryWhereHandler() {
        return queryWhereHandler;
    }

    public void setQueryWhereHandler(IQueryWhereHandler queryWhereHandler) {
        this.queryWhereHandler = queryWhereHandler;
    }
}
