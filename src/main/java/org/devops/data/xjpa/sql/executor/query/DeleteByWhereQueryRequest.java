package org.devops.data.xjpa.sql.executor.query;

import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.executor.LimitHandler;
import org.devops.data.xjpa.sql.executor.SortHandler;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 删除
 */
public class DeleteByWhereQueryRequest<K, V> extends AbstractQueryRequest<K, V> {

    /**
     * 分页限位
     */
    final LimitHandler limitHandler;

    /**
     * 排序
     */
    final SortHandler sortHandler;

    public DeleteByWhereQueryRequest(RepositoryContext<K, V> context, LimitHandler limitHandler, SortHandler sortHandler) {
        super(context);
        this.limitHandler = limitHandler;
        this.sortHandler = sortHandler;
    }

    public LimitHandler getLimitHandler() {
        return limitHandler;
    }

    public SortHandler getSortHandler() {
        return sortHandler;
    }
}
