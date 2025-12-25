package com.glee.xjpa.sql.where.operate;

public enum Condition {
    /**
     * 与
     */
    AND("&&"),
    /**
     * 或
     */
    OR("||");

    /**
     * 运算符
     */
    private final String operator;

    Condition(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public String getText() {
        return name().toLowerCase();
    }
}
