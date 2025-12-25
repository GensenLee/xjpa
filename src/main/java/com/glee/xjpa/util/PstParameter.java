package com.glee.xjpa.util;

import com.glee.xjpa.table.EntityTableField;

/**
 * @author GENSEN
 * @date 2022/12/12
 * @description PreparedStatement参数
 */
public class PstParameter {

    private final Object value;

    private final EntityTableField entityTableField;

    public PstParameter(Object value, EntityTableField entityTableField) {
        this.value = value;
        this.entityTableField = entityTableField;
    }

    public boolean isNull() {
        return value == null;
    }

    public Object getValue() {
        return value;
    }

    public EntityTableField getEntityTableField() {
        return entityTableField;
    }
}
