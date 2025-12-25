package com.glee.xjpa.join;


/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 表字段count计算，别名支持
 */
public class AliasSumJoinTableColumn extends DefaultAliasTableColumn {


    public AliasSumJoinTableColumn(JoinTable table, String column, String columnAlias) {
        super(table, column, columnAlias);
    }

    @Override
    public String getColumnLabel() {
        return "sum(" + concatTableAlias() + ") as " + getColumnAlias();
    }
}
