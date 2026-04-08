package com.glee.xjpa.io;

import com.glee.xjpa.io.groupby.GroupBy;
import com.glee.xjpa.io.having.GroupByHaving;
import com.glee.xjpa.io.include.IncludeBy;
import com.glee.xjpa.io.orderby.OrderBy;
import com.glee.xjpa.sql.where.usermodel.QueryWhereModel;

/**
 * @author GENSEN
 * @date 2026/3/23
 * @description
 */
public class GroupByInQuery implements XGroupByQuery<String> {

    private final Query delegate;

    private final GroupBy groupBy;

    private GroupByHaving having;

    public GroupByInQuery(Query delegate, GroupBy groupBy) {
        this.delegate = delegate;
        this.groupBy = groupBy;
    }


    @Override
    public GroupByInQuery having(GroupByHaving groupByHaving) {
        this.having = groupByHaving;
        return this;
    }

    @Override
    public GroupByInQuery where(QueryWhereModel<String> where) {
        delegate.where(where);
        return this;
    }

    @Override
    public GroupByInQuery limit(int start, int size) {
        delegate.limit(start, size);
        return this;
    }

    @Override
    public GroupByInQuery distinct() {
        delegate.distinct();
        return this;
    }

    @Override
    public GroupByInQuery orderBy(OrderBy orderBy) {
        delegate.orderBy(orderBy);
        return this;
    }

    @Override
    public GroupByInQuery include(IncludeBy includeBy) {
        delegate.include(includeBy);
        return this;
    }

    @Override
    public GroupByInQuery groupBy(GroupBy groupBy) {
        throw new UnsupportedOperationException("repeating group by");
    }


    QueryWhereModel<String> getWhere() {
        return delegate.getWhere();
    }

    QueryPage getQueryPage() {
        return delegate.getQueryPage();
    }

    boolean isDistinct() {
        return delegate.isDistinct();
    }

    IncludeBy getIncluding() {
        return delegate.getIncludeBy();
    }

    OrderBy getOrderBy() {
        return delegate.getOrderBy();
    }

    GroupBy getGroupBy() {
        return groupBy;
    }

    GroupByHaving getHaving() {
        return having;
    }
}
