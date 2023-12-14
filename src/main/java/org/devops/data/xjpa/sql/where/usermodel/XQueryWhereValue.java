package org.devops.data.xjpa.sql.where.usermodel;

import lombok.extern.slf4j.Slf4j;
import org.devops.core.utils.util.StringUtil;
import org.devops.data.xjpa.sql.where.QueryWhereUtil;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereNode;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 单值条件
 */
@Slf4j
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
                return StringUtil.isEmpty(column);
            default:
                return StringUtil.isEmpty(column) || value == null;
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
