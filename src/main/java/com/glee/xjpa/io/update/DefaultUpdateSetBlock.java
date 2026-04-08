package com.glee.xjpa.io.update;

/**
 * @author GENSEN
 * @date 2026/3/31
 * @description
 */
public class DefaultUpdateSetBlock implements UpdateSetBlock {

    private final Object value;

    public DefaultUpdateSetBlock(Object value) {
        this.value = value;
    }

    @Override
    public String toUpdateTemplateClause() {
        return "?";
    }

    @Override
    public Object getParameter() {
        return value;
    }
}
