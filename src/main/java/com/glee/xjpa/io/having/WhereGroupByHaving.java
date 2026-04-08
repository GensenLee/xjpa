package com.glee.xjpa.io.having;

import com.glee.xjpa.sql.where.QueryWhereUtil;
import com.glee.xjpa.sql.where.usermodel.QueryWhere;

/**
 * @author GENSEN
 * @date 2026/3/25
 * @description
 */
public class WhereGroupByHaving extends GroupByHaving {

    private final QueryWhere where;

    public WhereGroupByHaving(QueryWhere where) {
        this.where = where;
    }


    @Override
    public String toSqlClause() {
        return QueryWhereUtil.toWhereString(where, true);
    }
}
