package com.glee.xjpa.io.include;

/**
 * @author GENSEN
 * @date 2026/3/25
 * @description 单列
 */
public class IncludeSingleColumn extends IncludeBy {

    private final String column;

    public IncludeSingleColumn(String column) {
        this.column = column;
    }

    @Override
    public String toSqlClause() {
        return column;
    }
}
