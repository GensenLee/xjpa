package org.devops.data.xjpa.join;

import lombok.Getter;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 表字段count计算，别名支持
 */
@Getter
public class AliasSumJoinTableColumn extends DefaultAliasTableColumn {


    public AliasSumJoinTableColumn(JoinTable table, String column, String columnAlias) {
        super(table, column, columnAlias);
    }

    @Override
    public String getColumnLabel() {
        return "sum(" + concatTableAlias() + ") as " + getColumnAlias();
    }
}
