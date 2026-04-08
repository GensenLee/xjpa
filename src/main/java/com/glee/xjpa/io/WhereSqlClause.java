package com.glee.xjpa.io;

import com.glee.xjpa.sql.where.QueryWhereUtil;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;

/**
 * @author GENSEN
 * @date 2026/3/23
 * @description
 */
public class WhereSqlClause extends SqlClause {

    private final IQueryWhereObject queryWhereObject;

    public WhereSqlClause(IQueryWhereObject queryWhereObject) {
        this.queryWhereObject = queryWhereObject;
    }

    @Override
    public String toSqlClause() {
        return QueryWhereUtil.toWhereString(queryWhereObject);
    }
}
