package com.glee.xjpa.sql.executor;

public enum SortType {
    desc("desc", "倒序"),
    asc("asc", "正序"),

    plain("", "自定义");

    private final String operator;

    private final String name;

    SortType(String operator, String name) {
        this.operator = operator;
        this.name = name;
    }

    public String getOperator() {
        return operator;
    }

    public String getName() {
        return name;
    }
}