package com.glee.xjpa.io.orderby;

import com.glee.xjpa.io.column.TableColumn;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接查询降序
 */
public class JoiningOrderByDesc extends JoiningOrderBy {

    private final TableColumn column;

    public JoiningOrderByDesc(TableColumn column) {
        this.column = column;
    }

    @Override
    public String toSqlClause() {
        return column.getColumnLabel() + " desc";
    }
}
