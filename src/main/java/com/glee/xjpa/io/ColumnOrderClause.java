package com.glee.xjpa.io;

import com.glee.xjpa.io.column.TableColumn;

/**
 * @author GENSEN
 * @date 2026/3/23
 * @description 列排序
 */
public class ColumnOrderClause extends SqlClause {

    private final TableColumn column;

    private final String orderBy;

    public ColumnOrderClause(TableColumn column, String orderBy) {
        this.column = column;
        this.orderBy = orderBy;
    }

    @Override
    public String toSqlClause() {
        return column.getColumnLabel() + " " + orderBy;
    }
}
