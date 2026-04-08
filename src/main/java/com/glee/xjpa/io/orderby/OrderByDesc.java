package com.glee.xjpa.io.orderby;

/**
 * @author GENSEN
 * @date 2026/3/25
 * @description
 */
public class OrderByDesc extends OrderBy {

    private final String column;

    public OrderByDesc(String column) {
        this.column = column;
    }

    @Override
    public String toSqlClause() {
        return column + " desc";
    }
}
