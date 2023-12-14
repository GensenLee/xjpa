package org.devops.data.xjpa.repository;

/**
 * @author GENSEN
 * @date 2022/10/28
 * @description 更新操作
 */
public enum UpdateOperator {

    /**
     * 等
     */
    EQ("="),
    /**
     * 加
     */
    ADD("+"),
    /**
     * 减
     */
    SUB("-"),
    /**
     * 乘
     */
    MCL("*"),
    /**
     * 除
     */
    DIV("/");

    private final String operator;

    UpdateOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
