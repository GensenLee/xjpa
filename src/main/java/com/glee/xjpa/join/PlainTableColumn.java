package com.glee.xjpa.join;

/**
 * @author GENSEN
 * @date 2025/5/20
 * @description 字符串
 */
public class PlainTableColumn implements TableColumn {

    private final String label;

    public PlainTableColumn(String label) {
        this.label = label;
    }

    @Override
    public String getColumnLabel() {
        return label;
    }
}
