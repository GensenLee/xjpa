package com.glee.xjpa.io.groupby;

import com.glee.xjpa.io.column.TableColumn;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接查询单列分组
 */
public class JoiningGroupBySingleColumn extends JoiningGroupBy {

    private final TableColumn column;

    public JoiningGroupBySingleColumn(TableColumn column) {
        this.column = column;
    }

    @Override
    public String toSqlClause() {
        return column.getColumnLabel();
    }
}
