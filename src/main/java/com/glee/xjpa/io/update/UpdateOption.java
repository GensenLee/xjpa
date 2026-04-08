package com.glee.xjpa.io.update;

/**
 * @author GENSEN
 * @date 2026-01-21
 * @description 更新操作
 */
public enum UpdateOption {

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


    private final String symbol;

    UpdateOption(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
