package org.devops.data.xjpa.join;

import org.devops.data.xjpa.constant.XjpaConstant;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 表字段
 */
public class JoinTableColumn extends AbstractJoinTableColumn {

    private final JoinTable table;

    private final String column;

    public JoinTableColumn(JoinTable table, String column) {
        this.table = table;
        this.column = column;
    }


    @Override
    public String getColumnLabel() {
        return concatTableAlias();
    }

    protected String concatTableAlias() {
        return tableAlias + XjpaConstant.POINT_MARK + "`" + column + "`";
    }

    @Override
    JoinTable getJoinTable() {
        return table;
    }

    public JoinTable getTable() {
        return table;
    }

    public String getColumn() {
        return column;
    }
}
