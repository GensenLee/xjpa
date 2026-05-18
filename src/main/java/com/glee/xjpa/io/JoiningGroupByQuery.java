package com.glee.xjpa.io;

import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.io.groupby.JoiningGroupBy;
import com.glee.xjpa.io.having.JoiningGroupByHave;
import com.glee.xjpa.io.include.JoiningIncludeBy;
import com.glee.xjpa.io.orderby.JoiningOrderBy;
import com.glee.xjpa.sql.where.usermodel.QueryWhereModel;

/**
 * @author GENSEN
 * @date 2026/3/23
 * @description
 */
public class JoiningGroupByQuery implements XJoiningGroupByQuery<TableColumn> {

    private final JoiningQuery delegate;

    private final JoiningGroupBy joiningGroupBy;

    private JoiningGroupByHave groupByHave;

    public JoiningGroupByQuery(JoiningQuery delegate, JoiningGroupBy joiningGroupBy) {
        this.delegate = delegate;
        this.joiningGroupBy = joiningGroupBy;
    }


    @Override
    public JoiningGroupByQuery having(JoiningGroupByHave groupByHave) {
        this.groupByHave = groupByHave;
        return this;
    }

    @Override
    public JoiningGroupByQuery where(QueryWhereModel<TableColumn> where) {
        delegate.where(where);
        return this;
    }

    @Override
    public JoiningGroupByQuery limit(int start, int size) {
        delegate.limit(start, size);
        return this;
    }

    @Override
    public JoiningGroupByQuery distinct() {
        delegate.distinct();
        return this;
    }

    @Override
    public JoiningGroupByQuery orderBy(JoiningOrderBy orderBy) {
        delegate.orderBy(orderBy);
        return this;
    }

    @Override
    public JoiningGroupByQuery include(JoiningIncludeBy includeBy) {
        delegate.include(includeBy);
        return this;
    }


    @Override
    public JoiningGroupByQuery groupBy(JoiningGroupBy groupBy) {
        throw new UnsupportedOperationException("repeating group by");
    }


    public QueryWhereModel<TableColumn> getWhere() {
        return delegate.getWhere();
    }

    public QueryPage getQueryPage() {
        return delegate.getQueryPage();
    }

    public boolean isDistinct() {
        return delegate.isDistinct();
    }

    public JoiningIncludeBy getIncluding() {
        return delegate.getIncluding();
    }

    public JoiningOrderBy getOrderBy() {
        return delegate.getOrderBy();
    }

    public JoiningGroupBy getJoiningGroupBy() {
        return joiningGroupBy;
    }

    public JoiningGroupByHave getGroupByHave() {
        return groupByHave;
    }
}
