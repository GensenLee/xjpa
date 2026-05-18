package com.glee.xjpa.io.include;

import com.glee.xjpa.io.column.TableColumn;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接查询单列
 */
public class JoiningIncludeSingleColumn extends JoiningIncludeBy {

    private final TableColumn column;

    public JoiningIncludeSingleColumn(TableColumn column) {
        this.column = column;
    }

    @Override
    public String toSqlClause() {
        return column.getColumnLabel();
    }
}
