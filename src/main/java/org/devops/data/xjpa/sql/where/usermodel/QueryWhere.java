package org.devops.data.xjpa.sql.where.usermodel;

import org.devops.data.xjpa.sql.where.XQueryWhereExplorer;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;

import java.io.Serializable;

public class QueryWhere extends GenericQueryWhere<String> {

    public QueryWhere() {
        super();
    }

    public QueryWhere(IQueryWhereObject whereObject) {
        super(whereObject);
    }

    public <T extends Serializable> QueryWhere(String column, T value) {
        super(column, value);
    }

    public <T extends Serializable> QueryWhere(String column, T value, WhereOperator operator) {
        super(column, value, operator);
    }

    public <T extends Serializable> QueryWhere(String column, T value, WhereOperator operator, Condition condition) {
        super(column, value, operator, condition);
    }

    @Override
    public QueryWhere put(IQueryWhereObject value) {
        return (QueryWhere) super.put(value);
    }

    @Override
    public <T extends Serializable> QueryWhere andEqual(String column, T value) {
        return (QueryWhere) super.andEqual(column, value);
    }

    @Override
    public <T extends Iterable> QueryWhere andIn(String column, T value) {
        return (QueryWhere) super.andIn(column, value);
    }

    @Override
    public QueryWhere and(String column, WhereOperator operator) {
        return (QueryWhere) super.and(column, operator);
    }

    @Override
    public QueryWhere and(String column, Object value, WhereOperator operator) {
        return (QueryWhere) super.and(column, value, operator);
    }

    @Override
    public <T extends Serializable> QueryWhere orEqual(String column, T value) {
        return (QueryWhere) super.orEqual(column, value);
    }

    @Override
    public <T extends Iterable> QueryWhere orIn(String column, T value) {
        return (QueryWhere) super.orIn(column, value);
    }

    @Override
    public QueryWhere or(String column, WhereOperator operator) {
        return (QueryWhere) super.or(column, operator);
    }

    @Override
    public QueryWhere or(String column, Object value, WhereOperator operator) {
        return (QueryWhere) super.or(column, value, operator);
    }

    @Override
    public <T extends Serializable> QueryWhere andBetween(String column, T start, T end) {
        return (QueryWhere) super.andBetween(column, start, end);
    }

    @Override
    public <T extends Serializable> QueryWhere andNotBetween(String column, T start, T end) {
        return (QueryWhere) super.andNotBetween(column, start, end);
    }

    @Override
    public QueryWhere andNotNull(String column) {
        return (QueryWhere) super.andNotNull(column);
    }

    @Override
    public QueryWhere andIsNull(String column) {
        return (QueryWhere) super.andIsNull(column);
    }

    @Override
    public <T extends Serializable> QueryWhere orBetween(String column, T start, T end) {
        return (QueryWhere) super.orBetween(column, start, end);
    }

    @Override
    public <T extends Serializable> QueryWhere orNotBetween(String column, T start, T end) {
        return (QueryWhere) super.orNotBetween(column, start, end);
    }

    @Override
    public QueryWhere orNotNull(String column) {
        return (QueryWhere) super.orNotNull(column);
    }

    @Override
    public QueryWhere orIsNull(String column) {
        return (QueryWhere) super.orIsNull(column);
    }

    @Override
    public <T extends Serializable> QueryWhere equal(String column, T value, Condition condition) {
        return (QueryWhere) super.equal(column, value, condition);
    }

    @Override
    public QueryWhere add(String column, Object value, WhereOperator operator, Condition condition) {
        return (QueryWhere) super.add(column, value, operator, condition);
    }

    @Override
    public QueryWhere add(boolean valid, IQueryWhereObject value) {
        return (QueryWhere) super.add(valid, value);
    }

    @Override
    public <T extends Serializable> QueryWhere add(boolean valid, String column, T value) {
        return (QueryWhere) super.add(valid, column, value);
    }

    @Override
    public QueryWhere add(boolean valid, String column, WhereOperator operator) {
        return (QueryWhere) super.add(valid, column, operator);
    }

    @Override
    public QueryWhere add(boolean valid, String column, Object value, WhereOperator operator) {
        return (QueryWhere) super.add(valid, column, value, operator);
    }

    @Override
    public <T extends Serializable> QueryWhere add(boolean valid, String column, T value, Condition condition) {
        return (QueryWhere) super.add(valid, column, value, condition);
    }

    @Override
    public QueryWhere add(boolean valid, String column, Object value, WhereOperator operator, Condition condition) {
        return (QueryWhere) super.add(valid, column, value, operator, condition);
    }

    @Override
    public String toString() {
        XQueryWhereExplorer xQueryWhereExplorer = new XQueryWhereExplorer(true);
        accept(xQueryWhereExplorer);
        return xQueryWhereExplorer.getWhereString();
    }
}
