package com.glee.xjpa.io.orderby;

import com.glee.xjpa.io.column.TableColumn;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接查询升序
 */
public class JoiningOrderByAsc extends JoiningOrderBy {

    private final TableColumn column;

    public JoiningOrderByAsc(TableColumn column) {
        this.column = column;
    }

    @Override
    public String toSqlClause() {
        return column.getColumnLabel() + " asc";
    }
}
