package com.glee.xjpa.io.column;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description include 表字段
 * see org.devops.data.xjpa.join.Alias
 */
public class DefaultAliasTableColumn extends JoiningTableColumn implements AliasTableColumn, Serializable {

    private final String columnAlias;

    public DefaultAliasTableColumn(JoiningTable table, String column, String columnAlias) {
        super(table, column);
        this.columnAlias = columnAlias;
    }


    @Override
    public String getColumnLabel() {
        return super.getColumnLabel() + " as " + getColumnAlias();
    }


    @Override
    public String getColumnAlias() {
        return columnAlias;
    }
}
