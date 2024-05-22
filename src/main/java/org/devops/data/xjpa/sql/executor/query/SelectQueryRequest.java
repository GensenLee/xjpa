package org.devops.data.xjpa.sql.executor.query;

import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.executor.LimitHandler;
import org.devops.data.xjpa.sql.executor.SortHandler;

import java.util.Collection;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 查询
 */

public class SelectQueryRequest<K, V> extends AbstractQueryRequest<K, V> {

    /**
     *
     */
    private final LimitHandler limitHandler;

    /**
     * 排序
     */
    private final SortHandler sortHandler;

    private final Collection<String> includeColumns;

    private final boolean distinct;

    private final Collection<String> groupingColumns;

    private final String havingString;

    public SelectQueryRequest(RepositoryContext<K, V> context,
                              LimitHandler limitHandler,
                              SortHandler sortHandler,
                              Collection<String> includeColumns,
                              boolean distinct,
                              Collection<String> groupingColumns,
                              String havingString) {
        super(context);
        this.limitHandler = limitHandler;
        this.sortHandler = sortHandler;
        this.includeColumns = includeColumns;
        this.distinct = distinct;
        this.groupingColumns = groupingColumns;
        this.havingString = havingString;
    }

    public Collection<String> includeColumns() {
        return includeColumns;
    }

    public Collection<String> groupingColumns() {
        return groupingColumns;
    }

    public String getHavingString() {
        return havingString;
    }

    public LimitHandler getLimitHandler() {
        return limitHandler;
    }

    public SortHandler getSortHandler() {
        return sortHandler;
    }

    public boolean isDistinct() {
        return distinct;
    }
}
