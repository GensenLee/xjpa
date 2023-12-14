package org.devops.data.xjpa.join;

/**
 * @author GENSEN
 * @date 2023/7/5
 * @description 条件值
 */
public class WhereValue implements TableColumn{

    private final String value;

    public WhereValue(String value) {
        this.value = value;
    }

    @Override
    public String getColumnLabel() {
        return "'" + value + "'";
    }
}
