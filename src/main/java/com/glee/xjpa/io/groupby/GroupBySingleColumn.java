package com.glee.xjpa.io.groupby;

/**
 * @author GENSEN
 * @date 2026/3/25
 * @description 单列
 */
public class GroupBySingleColumn extends GroupBy {

    private final String column;

    public GroupBySingleColumn(String column) {
        this.column = column;
    }

    @Override
    public String toSqlClause() {
        return column;
    }
}
