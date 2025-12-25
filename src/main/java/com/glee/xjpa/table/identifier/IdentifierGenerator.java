package com.glee.xjpa.table.identifier;

/**
 * @author GENSEN
 * @date 2021/9/15 16:42
 * @description：id生成器
 */
public interface IdentifierGenerator<T> {

    /**
     * 生成一个id
     * @return
     */
    T next();
}
