package com.glee.xjpa.io.column;

/**
 * @author GENSEN
 * @date 2026/3/23
 * @description 连接表
 */
public interface JoiningTable {

    String getTableName();

    String getTableAlias();

    int getJoiningOrder();

}
