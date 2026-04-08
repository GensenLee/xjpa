package com.glee.xjpa.io;

import com.glee.xjpa.io.column.TableColumn;

/**
 * @author GENSEN
 * @date 2026/3/23
 * @description 包括列
 */
public class SingleColumnClause extends SqlClause {

    private final TableColumn column;

    public SingleColumnClause(TableColumn column) {
        this.column = column;
    }

    @Override
    public String toSqlClause() {
        return column.getColumnLabel();
    }
}
