package com.glee.xjpa.table;

/**
 * @author GENSEN
 * @date 2022/11/18
 * @description
 */
public interface TableFieldMetadata {
    String getField();

    String getComment();

    String getType();

    String getNull();

    String getExtra();

    String getPrivileges();

    String getKey();

    String getDefault();
}
