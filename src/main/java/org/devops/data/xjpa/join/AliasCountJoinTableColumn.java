package org.devops.data.xjpa.join;


/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 表字段count计算，别名支持
 */
public class AliasCountJoinTableColumn extends DefaultAliasTableColumn {


    public AliasCountJoinTableColumn(JoinTable table, String column, String columnAlias) {
        super(table, column, columnAlias);
    }

    @Override
    public String getColumnLabel() {
        return "count(" + concatTableAlias() + ") as " + getColumnAlias();
    }
}
