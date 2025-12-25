package com.glee.xjpa.sql.where.usermodel;

import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import com.glee.xjpa.sql.where.objects.IQueryWhereObjectVisitor;
import com.glee.xjpa.sql.where.operate.Condition;
import com.glee.xjpa.sql.where.operate.WhereOperator;
import com.glee.xjpa.sql.where.subquery.InlineSubQuery;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GenericQueryWhere<Column> implements QueryWhereModel<Column> {


    /**
     * 不定类型，根据条件设置变化
     */
    protected IQueryWhereObject whereObject;

    protected Condition condition;

    public GenericQueryWhere(IQueryWhereObject whereObject) {
        this.whereObject = whereObject;
    }

    public GenericQueryWhere() {
    }

    public <T extends Serializable>  GenericQueryWhere(Column column, T value) {
        this(column, value, WhereOperator.EQ);
    }

    public GenericQueryWhere(Column column, Object value, WhereOperator operator) {
        if (value instanceof Collection) {
            operator = WhereOperator.IN;
        }
        this.whereObject = new XQueryWhereValue(column, value, operator, Condition.AND);
    }

    public GenericQueryWhere(Column column, Object value, WhereOperator operator, Condition condition) {
        if (value instanceof Collection || (value != null && value.getClass().isArray())) {
            operator = WhereOperator.IN;
        }
        this.whereObject = new XQueryWhereValue(column, value, operator, condition);
    }

    /**
     * @param value
     */
    private GenericQueryWhere<Column> addToValue(IQueryWhereObject value){
        if (this.whereObject == null) {
            this.whereObject = value;
            return this;
        }

        if (!(this.whereObject instanceof XQueryWhereValues)) {
            ArrayList<IQueryWhereObject> values = new ArrayList<>();
            values.add(this.whereObject);
            this.whereObject = new XQueryWhereValues(values);
        }

        ((XQueryWhereValues) whereObject).children().add(value);

        return this;
    }

    @Override
    public GenericQueryWhere<Column> put(IQueryWhereObject value) {
        if (value == null || value.isEmpty()) {
            return this;
        }
        return addToValue(value);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> andBetween(Column column, T start, T end) {
        Assert.notNull(start, "between start value required");
        Assert.notNull(end, "between end value required");

        return and(column, new Object[]{start, end}, WhereOperator.BETWEEN);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> andNotBetween(Column column, T start, T end) {
        Assert.notNull(start, "between start value required");
        Assert.notNull(end, "between end value required");

        return and(column, new Object[]{start, end}, WhereOperator.NOT_BETWEEN);
    }

    @Override
    public GenericQueryWhere<Column> andNotNull(Column column) {
        return and(column, null, WhereOperator.IS_NOT_NULL);
    }

    @Override
    public GenericQueryWhere<Column> andIsNull(Column column) {
        return and(column, null, WhereOperator.IS_NULL);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> orBetween(Column column, T start, T end) {
        Assert.notNull(start, "between start value required");
        Assert.notNull(end, "between end value required");

        return or(column, new Object[]{start, end}, WhereOperator.BETWEEN);
    }



    @Override
    public <T extends Serializable> GenericQueryWhere<Column> orNotBetween(Column column, T start, T end) {
        Assert.notNull(start, "between start value required");
        Assert.notNull(end, "between end value required");

        return or(column, new Object[]{start, end}, WhereOperator.NOT_BETWEEN);
    }

    @Override
    public GenericQueryWhere<Column> orNotNull(Column column) {
        return or(column, null, WhereOperator.IS_NOT_NULL);
    }

    @Override
    public GenericQueryWhere<Column> orIsNull(Column column) {
        return or(column, null, WhereOperator.IS_NULL);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> andEqual(Column column, T value) {
        return add(column, value, WhereOperator.EQ, Condition.AND);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T extends Iterable> GenericQueryWhere<Column> andIn(Column column, T value) {
        return and(column, value, WhereOperator.IN);
    }

    @Override
    public GenericQueryWhere<Column> and(Column column, WhereOperator operator) {
        return switch (operator) {
            case IS_NULL, IS_NOT_NULL -> add(column, null, operator, Condition.AND);
            default -> this;
        };
    }

    @Override
    public GenericQueryWhere<Column> and(Column column, Object value, WhereOperator operator) {
        return add(column, value, operator, Condition.AND);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> orEqual(Column column, T value) {
        return add(column, value, WhereOperator.EQ, Condition.OR);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T extends Iterable> GenericQueryWhere<Column> orIn(Column column, T value) {
        return or(column, value, WhereOperator.IN);
    }

    @Override
    public GenericQueryWhere<Column> or(Column column, WhereOperator operator) {
        return switch (operator) {
            case IS_NULL, IS_NOT_NULL -> add(column, null, operator, Condition.OR);
            default -> this;
        };
    }

    @Override
    public GenericQueryWhere<Column> or(Column column, Object value, WhereOperator operator) {
        return add(column, value, operator, Condition.OR);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> equal(Column column, T value, Condition condition) {
        return add(column, value, WhereOperator.EQ, condition);
    }

    @Override
    public GenericQueryWhere<Column> add(Column column, Object value, WhereOperator operator, Condition condition) {
        put(new XQueryWhereValue(column, value, operator, condition));
        return this;
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> andNotEqual(Column column, T value) {
        return add(column, value, WhereOperator.NEQ, Condition.AND);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> andLike(Column column, T value) {
        return add(column, value, WhereOperator.LIKE, Condition.AND);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> andLeftLike(Column column, T value) {
        return add(column, value, WhereOperator.LIKE_RIGHT, Condition.AND);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> andRightLike(Column column, T value) {
        return add(column, value, WhereOperator.LIKE_LEFT, Condition.AND);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T extends Iterable> GenericQueryWhere<Column> andNotIn(Column column, T value) {
        return add(column, value, WhereOperator.NOT_IN, Condition.AND);
    }

    @Override
    public GenericQueryWhere<Column> andIn(Column column, InlineSubQuery value) {
        return add(column, value, WhereOperator.IN, Condition.AND);
    }

    @Override
    public GenericQueryWhere<Column> andNotIn(Column column, InlineSubQuery value) {
        return add(column, value, WhereOperator.NOT_IN, Condition.AND);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> orNotEqual(Column column, T value) {
        return add(column, value, WhereOperator.NEQ, Condition.OR);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> orLike(Column column, T value) {
        return add(column, value, WhereOperator.LIKE, Condition.OR);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> orLeftLike(Column column, T value) {
        return add(column, value, WhereOperator.LIKE_RIGHT, Condition.OR);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> orRightLike(Column column, T value) {
        return add(column, value, WhereOperator.LIKE_LEFT, Condition.OR);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T extends Iterable> GenericQueryWhere<Column> orNotIn(Column column, T value) {
        return add(column, value, WhereOperator.NOT_IN, Condition.OR);
    }

    @Override
    public GenericQueryWhere<Column> orIn(Column column, InlineSubQuery value) {
        return add(column, value, WhereOperator.IN, Condition.OR);
    }

    @Override
    public GenericQueryWhere<Column> orNotIn(Column column, InlineSubQuery value) {
        return add(column, value, WhereOperator.NOT_IN, Condition.OR);
    }

    @Override
    public <T extends Serializable> QueryWhereModel<Column> andGreaterThan(Column column, T value) {
        return add(column, value, WhereOperator.GT, Condition.AND);
    }

    @Override
    public <T extends Serializable> QueryWhereModel<Column> andLessThan(Column column, T value) {
        return add(column, value, WhereOperator.LT, Condition.AND);
    }

    @Override
    public <T extends Serializable> QueryWhereModel<Column> orGreaterThan(Column column, T value) {
        return add(column, value, WhereOperator.GT, Condition.OR);
    }

    @Override
    public <T extends Serializable> QueryWhereModel<Column> orLessThan(Column column, T value) {
        return add(column, value, WhereOperator.LT, Condition.OR);
    }

    @Override
    public <T extends Serializable> QueryWhereModel<Column> andEqualOrGreaterThan(Column column, T value) {
        return add(column, value, WhereOperator.EGT, Condition.AND);
    }

    @Override
    public <T extends Serializable> QueryWhereModel<Column> andEqualOrLessThan(Column column, T value) {
        return add(column, value, WhereOperator.ELT, Condition.AND);
    }

    @Override
    public <T extends Serializable> QueryWhereModel<Column> orEqualOrGreaterThan(Column column, T value) {
        return add(column, value, WhereOperator.EGT, Condition.OR);
    }

    @Override
    public <T extends Serializable> QueryWhereModel<Column> orEqualOrLessThan(Column column, T value) {
        return add(column, value, WhereOperator.ELT, Condition.OR);
    }

    public GenericQueryWhere<Column> add(boolean valid, IQueryWhereObject value) {
        if (!valid) {
            return this;
        }
        return put(value);
    }

    public <T extends Serializable> GenericQueryWhere<Column> add(boolean valid, Column column, T value) {
        if (!valid) {
            return this;
        }
        return andEqual(column, value);
    }

    public GenericQueryWhere<Column> add(boolean valid, Column column, WhereOperator operator) {
        if (!valid) {
            return this;
        }
        return and(column, operator);
    }

    public GenericQueryWhere<Column> add(boolean valid, Column column, Object value, WhereOperator operator) {
        if (!valid) {
            return this;
        }
        return and(column, value, operator);
    }

    public <T extends Serializable> GenericQueryWhere<Column> add(boolean valid, Column column, T value, Condition condition) {
        if (!valid) {
            return this;
        }
        return equal(column, value, condition);
    }

    public GenericQueryWhere<Column> add(boolean valid, Column column, Object value, WhereOperator operator, Condition condition) {
        if (!valid) {
            return this;
        }
        return add(column, value, operator, condition);
    }


    @Override
    public Condition attachCondition() {
        return condition == null ? Condition.AND : condition;
    }

    @Override
    public Map<Integer, Object> indexValues(final AtomicInteger index) {
        if (whereObject == null) {
            return Collections.emptyMap();
        }
        return whereObject.indexValues(index);
    }

    @Override
    public boolean isEmpty() {
        return whereObject == null || whereObject.isEmpty();
    }

    @Override
    public GenericQueryWhere<Column> condition(Condition condition) {
        this.condition = condition;
        if (this.whereObject != null) {
            this.whereObject.condition(condition);
        }
        return this;
    }

    @Override
    public void accept(IQueryWhereObjectVisitor visitor) {
        if (whereObject != null) {
            whereObject.accept(visitor);
        }
    }

}
