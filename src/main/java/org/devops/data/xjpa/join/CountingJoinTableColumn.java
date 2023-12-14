package org.devops.data.xjpa.join;

import lombok.Getter;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 表字段count计算
 */
@Getter
public class CountingJoinTableColumn extends JoinTableColumn{

    public CountingJoinTableColumn(JoinTable table, String column) {
        super(table, column);
    }


    @Override
    public String getColumnLabel() {
        return "count(" + super.getColumnLabel() + ")";
    }
}
