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
 * @description SQL 模板工具类
 */
public class SqlTemplate {

    /**
     * 构建查询 SQL
     */
    public static String buildSelectSql(boolean distinct, IncludeBy includeBy, String tableName, 
                                        IQueryWhereObject where, GroupBy groupBy, 
                                        GroupByHaving having, OrderBy orderBy, QueryPage page) {
        StringBuilder sqlBuilder = new StringBuilder("select");
        
        if (distinct) {
            sqlBuilder.append(" distinct");
        }
        
        if (includeBy == null) {
            sqlBuilder.append(" *");
        } else {
            sqlBuilder.append(" ").append(includeBy.toSqlClause());
        }
        
        sqlBuilder.append(" from ").append(tableName);
        
        if (where != null && !where.isEmpty()) {
            sqlBuilder.append(" where ").append(QueryWhereUtil.toWhereString(where));
        }
        
        if (groupBy != null) {
            sqlBuilder.append(" group by ").append(groupBy.toSqlClause());
        }
        
        if (having != null) {
            sqlBuilder.append(" having ").append(having.toSqlClause());
        }
        
        if (orderBy != null) {
            sqlBuilder.append(" order by ").append(orderBy.toSqlClause());
        }
        
        if (page != null) {
            sqlBuilder.append(" limit ").append(page.start()).append(",").append(page.size());
        }
        
        return sqlBuilder.toString();
    }

    /**
     * 构建 Count SQL
     */
    public static String buildCountSql(String tableName, IQueryWhereObject where) {
        StringBuilder sqlBuilder = new StringBuilder("select count(*) from ").append(tableName);
        
        if (where != null && !where.isEmpty()) {
            sqlBuilder.append(" where ").append(QueryWhereUtil.toWhereString(where));
        }
        
        return sqlBuilder.toString();
    }

    /**
     * 构建 Delete SQL
     */
    public static String buildDeleteSql(String tableName, IQueryWhereObject where) {
        StringBuilder sqlBuilder = new StringBuilder("delete from ").append(tableName);
        
        if (where != null && !where.isEmpty()) {
            sqlBuilder.append(" where ").append(QueryWhereUtil.toWhereString(where));
        }
        
        return sqlBuilder.toString();
    }

}