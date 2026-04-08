package com.glee.xjpa.io.column;

import com.glee.xjpa.constant.XJpaConstant;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 表字段
 */
public class JoiningTableColumn extends AbstractJoinTableColumn {

    private final JoiningTable table;

    private final String column;

    public JoiningTableColumn(JoiningTable table, String column) {
        this.table = table;
        this.column = column;
    }


    @Override
    public String getColumnLabel() {
        return concatTableAlias();
    }

    protected String concatTableAlias() {
        return tableAlias + XJpaConstant.POINT_MARK + "`" + column + "`";
    }

    public JoiningTable getTable() {
        return table;
    }

    public String getColumn() {
        return column;
    }
}
