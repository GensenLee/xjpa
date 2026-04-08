package com.glee.xjpa.sql;

import com.glee.xjpa.io.QueryPage;
import com.glee.xjpa.io.groupby.GroupBy;
import com.glee.xjpa.io.having.GroupByHaving;
import com.glee.xjpa.io.include.IncludeBy;
import com.glee.xjpa.io.orderby.OrderBy;
import com.glee.xjpa.sql.where.QueryWhereUtil;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;

/**
 * @author GENSEN
 * @date 2026/3/24
 * @description
 */
public class SqlTemplate {

    private final StringBuilder sqlBuilder;

    public SqlTemplate(String prefix) {
        this.sqlBuilder = new StringBuilder(prefix);
    }

    public void setDistinct(boolean distinct) {
        if (distinct) {
            sqlBuilder.append(" distinct");
        }
    }

    public void setIncludeBy(IncludeBy includeBy) {
        if (includeBy == null) {
            sqlBuilder.append(" *");
        } else {
            sqlBuilder.append(" ").append(includeBy.toSqlClause());
        }
    }

    public void setFromTable(String tableName) {
        sqlBuilder.append(" from ").append(tableName);
    }

    public void setWhere(IQueryWhereObject where) {
        if (where == null || where.isEmpty()) {
            return;
        }
        sqlBuilder.append(" where ").append(QueryWhereUtil.toWhereString(where));
    }

    public void setGroupBy(GroupBy groupBy) {
        if (groupBy == null) {
            return;
        }
        sqlBuilder.append(" group by ").append(groupBy.toSqlClause());
    }

    public void setHaving(GroupByHaving groupByHaving) {
        if (groupByHaving == null) {
            return;
        }
        sqlBuilder.append(" having ").append(groupByHaving.toSqlClause());
    }

    public void setOrderBy(OrderBy orderBy) {
        if (orderBy == null) {
            return;
        }
        sqlBuilder.append(" order by ").append(orderBy.toSqlClause());
    }

    public void setPage(QueryPage page) {
        if (page != null) {
            sqlBuilder.append(" limit ").append(page.start()).append(",").append(page.size());
        }
    }


    public String getSqlString() {
        return sqlBuilder.toString();
    }

}
