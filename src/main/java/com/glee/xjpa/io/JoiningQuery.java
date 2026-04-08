package com.glee.xjpa.io;

import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.io.groupby.JoiningGroupBy;
import com.glee.xjpa.io.include.JoiningIncludeBy;
import com.glee.xjpa.io.orderby.JoiningOrderBy;
import com.glee.xjpa.sql.where.usermodel.QueryWhereModel;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description
 */
public class JoiningQuery implements XJoiningQuery<TableColumn> {

    private QueryWhereModel<TableColumn> where;

    private QueryPage queryPage;

    private boolean distinct = false;

    private JoiningIncludeBy including;

    private JoiningOrderBy orderBy;

    @Override
    public JoiningQuery where(QueryWhereModel<TableColumn> where) {
        this.where = where;
        return this;
    }

    @Override
    public JoiningQuery limit(int start, int size) {
        this.queryPage = new QueryPage(Math.max(0, start), Math.max(0, size));
        return this;
    }

    @Override
    public JoiningQuery distinct() {
        this.distinct = true;
        return this;
    }

    @Override
    public JoiningQuery orderBy(JoiningOrderBy orderBy) {
        this.orderBy = orderBy;
        return this;
    }


    @Override
    public JoiningQuery include(JoiningIncludeBy including) {
        this.including = including;
        return this;
    }


    @Override
    public JoiningGroupByQuery groupBy(JoiningGroupBy groupBy) {
        return new JoiningGroupByQuery(this, groupBy);
    }

    public QueryWhereModel<TableColumn> getWhere() {
        return where;
    }

    QueryPage getQueryPage() {
        return queryPage;
    }

    boolean isDistinct() {
        return distinct;
    }

    JoiningIncludeBy getIncluding() {
        return including;
    }

    JoiningOrderBy getOrderBy() {
        return orderBy;
    }
}