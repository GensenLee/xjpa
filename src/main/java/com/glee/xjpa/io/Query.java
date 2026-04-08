package com.glee.xjpa.io;

import com.glee.xjpa.io.groupby.GroupBy;
import com.glee.xjpa.io.include.IncludeBy;
import com.glee.xjpa.io.orderby.OrderBy;
import com.glee.xjpa.sql.SqlTemplate;
import com.glee.xjpa.sql.where.usermodel.QueryWhereModel;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description
 */
public class Query implements XQuery<String> {

    private QueryWhereModel<String> where;

    private QueryPage queryPage;

    private boolean distinct = false;

    private IncludeBy includeBy;

    private OrderBy orderBy;

    public Query() {
    }

    @Override
    public Query where(QueryWhereModel<String> where) {
        this.where = where;
        return this;
    }

    @Override
    public Query limit(int start, int size) {
        this.queryPage = new QueryPage(Math.max(0, start), Math.max(0, size));
        return this;
    }

    @Override
    public Query distinct() {
        this.distinct = true;
        return this;
    }

    @Override
    public Query orderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
        return this;
    }


    @Override
    public Query include(IncludeBy includeBy) {
        this.includeBy = includeBy;
        return this;
    }


    @Override
    public GroupByInQuery groupBy(GroupBy groupBy) {
        return new GroupByInQuery(this, groupBy);
    }

    @Override
    public String toString() {
        SqlTemplate sqlTemplate = new SqlTemplate("");
        sqlTemplate.setDistinct(distinct);
        sqlTemplate.setIncludeBy(includeBy);
        sqlTemplate.setFromTable("?");
        sqlTemplate.setWhere(where);
        sqlTemplate.setOrderBy(orderBy);
        sqlTemplate.setPage(queryPage);
        return sqlTemplate.getSqlString();
    }

    QueryWhereModel<String> getWhere() {
        return where;
    }

    QueryPage getQueryPage() {
        return queryPage;
    }

    boolean isDistinct() {
        return distinct;
    }

    IncludeBy getIncludeBy() {
        return includeBy;
    }

    OrderBy getOrderBy() {
        return orderBy;
    }
}