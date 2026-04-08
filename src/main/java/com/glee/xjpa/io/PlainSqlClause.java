package com.glee.xjpa.io;

/**
 * @author GENSEN
 * @date 2026/3/23
 * @description 固定字符串子句
 */
public class PlainSqlClause extends SqlClause {

    private final String sqlClause;

    public PlainSqlClause(String sqlClause) {
        this.sqlClause = sqlClause;
    }

    @Override
    public String toSqlClause() {
        return sqlClause;
    }

}
