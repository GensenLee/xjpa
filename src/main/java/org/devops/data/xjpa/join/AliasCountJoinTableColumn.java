package org.devops.data.xjpa.join;

import lombok.Getter;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 表字段count计算，别名支持
 */
@Getter
public class AliasCountJoinTableColumn extends DefaultAliasTableColumn {


    public AliasCountJoinTableColumn(JoinTable table, String column, String columnAlias) {
        super(table, column, columnAlias);
    }

    @Override
    public String getColumnLabel() {
        return "count(" + concatTableAlias() + ") as " + getColumnAlias();
    }
}
