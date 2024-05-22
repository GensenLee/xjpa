package org.devops.data.xjpa.sql.where.usermodel;

import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObjectVisitor;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;
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
    public <T extends Serializable> IQueryWhereAcceptor<Column> andBetween(Column column, T start, T end) {
        Assert.notNull(start, "between start value required");
        Assert.notNull(end, "between end value required");

        return and(column, new Object[]{start, end}, WhereOperator.BETWEEN);
    }

    @Override
    public <T extends Serializable> IQueryWhereAcceptor<Column> andNotBetween(Column column, T start, T end) {
        Assert.notNull(start, "between start value required");
        Assert.notNull(end, "between end value required");

        return and(column, new Object[]{start, end}, WhereOperator.NOT_BETWEEN);
    }

    @Override
    public IQueryWhereAcceptor<Column> andNotNull(Column column) {
        return and(column, null, WhereOperator.IS_NOT_NULL);
    }

    @Override
    public IQueryWhereAcceptor<Column> andIsNull(Column column) {
        return and(column, null, WhereOperator.IS_NULL);
    }

    @Override
    public <T extends Serializable> IQueryWhereAcceptor<Column> orBetween(Column column, T start, T end) {
        Assert.notNull(start, "between start value required");
        Assert.notNull(end, "between end value required");

        return or(column, new Object[]{start, end}, WhereOperator.BETWEEN);
    }



    @Override
    public <T extends Serializable> IQueryWhereAcceptor<Column> orNotBetween(Column column, T start, T end) {
        Assert.notNull(start, "between start value required");
        Assert.notNull(end, "between end value required");

        return or(column, new Object[]{start, end}, WhereOperator.NOT_BETWEEN);
    }

    @Override
    public IQueryWhereAcceptor<Column> orNotNull(Column column) {
        return or(column, null, WhereOperator.IS_NOT_NULL);
    }

    @Override
    public IQueryWhereAcceptor<Column> orIsNull(Column column) {
        return or(column, null, WhereOperator.IS_NULL);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> andEqual(Column column, T value) {
        return add(column, value, WhereOperator.EQ, Condition.AND);
    }

    @Override
    public <T extends Iterable> GenericQueryWhere<Column> andIn(Column column, T value) {
        return and(column, value, WhereOperator.IN);
    }

    @Override
    public GenericQueryWhere<Column> and(Column column, WhereOperator operator) {
        switch (operator) {
            case IS_NULL:case IS_NOT_NULL:
                return add(column, null, operator, Condition.AND);
            default:
                return this;
        }
    }

    @Override
    public GenericQueryWhere<Column> and(Column column, Object value, WhereOperator operator) {
        return add(column, value, operator, Condition.AND);
    }

    @Override
    public <T extends Serializable> GenericQueryWhere<Column> orEqual(Column column, T value) {
        return add(column, value, WhereOperator.EQ, Condition.OR);
    }

    @Override
    public <T extends Iterable> GenericQueryWhere<Column> orIn(Column column, T value) {
        return or(column, value, WhereOperator.IN);
    }

    @Override
    public GenericQueryWhere<Column> or(Column column, WhereOperator operator) {
        switch (operator) {
            case IS_NULL:case IS_NOT_NULL:
                return add(column, null, operator, Condition.OR);
            default:
                return this;
        }
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
