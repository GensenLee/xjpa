package com.glee.xjpa.io;

/**
 * @author GENSEN
 * @date 2026/3/23
 * @description sql子句
 */
public abstract class SqlClause {

    /**
     * @return sql子句
     */
    public abstract String toSqlClause();

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof SqlClause qsc) {
            return this.toSqlClause().equalsIgnoreCase(qsc.toSqlClause());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toSqlClause().hashCode();
    }
}
