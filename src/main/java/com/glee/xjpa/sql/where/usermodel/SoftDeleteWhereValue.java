package com.glee.xjpa.sql.where.usermodel;

/**
 * @author GENSEN
 * @date 2025/7/22
 * @description 逻辑删除条件
 */
public class SoftDeleteWhereValue extends XQueryWhereValue {

    public SoftDeleteWhereValue(Object column, Object value) {
        super(column, value);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
