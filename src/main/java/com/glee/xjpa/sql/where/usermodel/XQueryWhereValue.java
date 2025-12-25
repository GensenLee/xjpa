package com.glee.xjpa.sql.where.usermodel;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.sql.where.QueryWhereUtil;
import com.glee.xjpa.sql.where.objects.IQueryWhereNode;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import com.glee.xjpa.sql.where.operate.Condition;
import com.glee.xjpa.sql.where.operate.WhereOperator;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 单值条件
 */
public class XQueryWhereValue implements Serializable, IQueryWhereNode {


    private final Object column;

    private final Object value;

    private final WhereOperator operator;
    private Condition condition;

    public XQueryWhereValue(Object column, Object value, WhereOperator operator, Condition condition) {
        this.column = column;
        this.value = value;
        this.operator = operator;
        this.condition = condition;
    }

    public XQueryWhereValue(Object column, Object value, WhereOperator operator) {
        this(column, value, operator, Condition.AND);
    }


    public XQueryWhereValue(Object column, Object value) {
        this(column, value, WhereOperator.EQ);
    }

    @Override
    public Condition attachCondition() {
        return condition == null ? Condition.AND : condition;
    }

    @Override
    public Map<Integer, Object> indexValues(final AtomicInteger index) {
        return QueryWhereUtil.exportParameters(operator, value, index);
    }

    @Override
    public boolean isEmpty() {
        switch (operator) {
            case IS_NULL:case IS_NOT_NULL:
                return Objects.isNull(column) || StrUtil.isEmpty(String.valueOf(column));
            default:
                return Objects.isNull(column) || StrUtil.isEmpty(String.valueOf(column)) || value == null;
        }

    }

    @Override
    public IQueryWhereObject condition(Condition condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public Object getColumn() {
        return column;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public WhereOperator getOperator() {
        return operator;
    }
}
