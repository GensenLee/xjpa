package org.devops.data.xjpa.sql.where.usermodel;

import org.devops.data.xjpa.sql.where.XQueryWhereExplorer;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;

public class QueryWhere extends GenericQueryWhere<String> {

    public QueryWhere() {
        super();
    }

    public QueryWhere(IQueryWhereObject whereObject) {
        super(whereObject);
    }

    public QueryWhere(String column, Object value) {
        super(column, value);
    }

    public QueryWhere(String column, Object value, WhereOperator operator) {
        super(column, value, operator);
    }

    public QueryWhere(String column, Object value, WhereOperator operator, Condition condition) {
        super(column, value, operator, condition);
    }

    @Override
    public QueryWhere put(IQueryWhereObject value) {
        return (QueryWhere) super.put(value);
    }

    @Override
    public QueryWhere andEqual(String column, Object value) {
        return (QueryWhere) super.andEqual(column, value);
    }

    @Override
    public QueryWhere andIn(String column, Object value) {
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
    public QueryWhere orEqual(String column, Object value) {
        return (QueryWhere) super.orEqual(column, value);
    }

    @Override
    public QueryWhere orIn(String column, Object value) {
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
    public QueryWhere andBetween(String column, Object start, Object end) {
        return (QueryWhere) super.andBetween(column, start, end);
    }

    @Override
    public QueryWhere andNotBetween(String column, Object start, Object end) {
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
    public QueryWhere orBetween(String column, Object start, Object end) {
        return (QueryWhere) super.orBetween(column, start, end);
    }

    @Override
    public QueryWhere orNotBetween(String column, Object start, Object end) {
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
    public QueryWhere equal(String column, Object value, Condition condition) {
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
    public QueryWhere add(boolean valid, String column, Object value) {
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
    public QueryWhere add(boolean valid, String column, Object value, Condition condition) {
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
