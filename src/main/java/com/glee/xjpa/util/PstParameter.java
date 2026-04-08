package com.glee.xjpa.util;

import com.glee.xjpa.table.EntityTableField;

/**
 * @author GENSEN
 * @date 2022/12/12
 * @description PreparedStatement参数
 */
public record PstParameter(Object value, EntityTableField entityTableField) {

    public boolean isNull() {
        return value == null;
    }
}
