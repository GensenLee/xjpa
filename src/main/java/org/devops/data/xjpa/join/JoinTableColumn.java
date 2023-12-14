package org.devops.data.xjpa.join;

import lombok.Getter;
import org.devops.core.utils.constant.CommonConstant;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 表字段
 */
@Getter
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
        return tableAlias + CommonConstant.POINT_MARK + "`" + column + "`";
    }

    @Override
    JoinTable getJoinTable() {
        return table;
    }

}
