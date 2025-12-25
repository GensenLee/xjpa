package com.glee.xjpa.sql.executor.query;

import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.sql.executor.SortHandler;
import com.glee.xjpa.sql.executor.LimitHandler;
import com.glee.xjpa.sql.executor.UpdateValueHandler;

import java.util.Set;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 根据where更新条件
 */
public class UpdateByWhereQueryRequest<K, V> extends AbstractQueryRequest<K, V> {

    /**
     * 分页限位
     */
    final LimitHandler limitHandler;

    /**
     * 排序
     */
    final SortHandler sortHandler;

    /**
     *
     */
    final UpdateValueHandler updateValueHandler;


    public UpdateByWhereQueryRequest(RepositoryContext<K, V> context,
                                     LimitHandler limitHandler,
                                     SortHandler sortHandler,
                                     UpdateValueHandler updateValueHandler) {
        super(context);
        this.limitHandler = limitHandler;
        this.sortHandler = sortHandler;
        this.updateValueHandler = updateValueHandler;
    }

    public Set<String> includeColumns() {
        return null;
    }

    public LimitHandler getLimitHandler() {
        return limitHandler;
    }

    public SortHandler getSortHandler() {
        return sortHandler;
    }

    public UpdateValueHandler getUpdateValueHandler() {
        return updateValueHandler;
    }
}
