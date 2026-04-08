package com.glee.xjpa.io.orderby;

/**
 * @author GENSEN
 * @date 2026/3/25
 * @description
 */
public class OrderByAsc extends OrderBy {

    private final String column;

    public OrderByAsc(String column) {
        this.column = column;
    }

    @Override
    public String toSqlClause() {
        return column + " asc";
    }
}
