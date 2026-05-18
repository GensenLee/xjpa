package com.glee.xjpa.sql.where.usermodel;

import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.sql.where.JoiningQueryWhereExplorer;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import com.glee.xjpa.sql.where.operate.Condition;
import com.glee.xjpa.sql.where.operate.WhereOperator;
import com.glee.xjpa.sql.where.subquery.InlineSubQuery;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接查询 Where 条件
 */
public class JoiningQueryWhere extends GenericQueryWhere<TableColumn> {

    public JoiningQueryWhere() {
    }

    public JoiningQueryWhere(IQueryWhereObject whereObject) {
        super(whereObject);
    }

    public <T extends Serializable> JoiningQueryWhere(TableColumn column, T value) {
        super(column, value);
    }

    public JoiningQueryWhere(TableColumn column, Object value, WhereOperator operator) {
        super(column, value, operator);
    }

    public JoiningQueryWhere(TableColumn column, Object value, WhereOperator operator, Condition condition) {
        super(column, value, operator, condition);
    }

    @Override
    public String toString() {
        JoiningQueryWhereExplorer explorer = new JoiningQueryWhereExplorer();
        accept(explorer);
        return explorer.getWhereString();
    }

    public Condition getCondition() {
        return condition;
    }

    @Override
    public JoiningQueryWhere put(IQueryWhereObject value) {
        if (value == null || value.isEmpty()) {
            return this;
        }
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.whereObject = value;
        return (JoiningQueryWhere) super.put(where);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andEqual(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andEqual(column, value);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T extends Iterable> JoiningQueryWhere andIn(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andIn(column, value);
    }

    @Override
    public JoiningQueryWhere andIn(TableColumn column, InlineSubQuery value) {
        return (JoiningQueryWhere) super.and(column, value, WhereOperator.IN);
    }

    @Override
    public JoiningQueryWhere and(TableColumn column, WhereOperator operator) {
        return (JoiningQueryWhere) super.and(column, operator);
    }

    @Override
    public JoiningQueryWhere and(TableColumn column, Object value, WhereOperator operator) {
        return (JoiningQueryWhere) super.and(column, value, operator);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orEqual(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orEqual(column, value);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T extends Iterable> JoiningQueryWhere orIn(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orIn(column, value);
    }

    @Override
    public JoiningQueryWhere orIn(TableColumn column, InlineSubQuery value) {
        return (JoiningQueryWhere) super.or(column, value, WhereOperator.IN);
    }

    @Override
    public JoiningQueryWhere or(TableColumn column, WhereOperator operator) {
        return (JoiningQueryWhere) super.or(column, operator);
    }

    @Override
    public JoiningQueryWhere or(TableColumn column, Object value, WhereOperator operator) {
        return (JoiningQueryWhere) super.or(column, value, operator);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andBetween(TableColumn column, T start, T end) {
        return (JoiningQueryWhere) super.andBetween(column, start, end);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andNotBetween(TableColumn column, T start, T end) {
        return (JoiningQueryWhere) super.andNotBetween(column, start, end);
    }

    @Override
    public JoiningQueryWhere andNotNull(TableColumn column) {
        return (JoiningQueryWhere) super.andNotNull(column);
    }

    @Override
    public JoiningQueryWhere andIsNull(TableColumn column) {
        return (JoiningQueryWhere) super.andIsNull(column);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orBetween(TableColumn column, T start, T end) {
        return (JoiningQueryWhere) super.orBetween(column, start, end);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orNotBetween(TableColumn column, T start, T end) {
        return (JoiningQueryWhere) super.orNotBetween(column, start, end);
    }

    @Override
    public JoiningQueryWhere orNotNull(TableColumn column) {
        return (JoiningQueryWhere) super.orNotNull(column);
    }

    @Override
    public JoiningQueryWhere orIsNull(TableColumn column) {
        return (JoiningQueryWhere) super.orIsNull(column);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere equal(TableColumn column, T value, Condition condition) {
        return (JoiningQueryWhere) super.equal(column, value, condition);
    }

    @Override
    public JoiningQueryWhere add(TableColumn column, Object value, WhereOperator operator, Condition condition) {
        return (JoiningQueryWhere) super.add(column, value, operator, condition);
    }

    @Override
    public JoiningQueryWhere add(boolean valid, IQueryWhereObject value) {
        return (JoiningQueryWhere) super.add(valid, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere add(boolean valid, TableColumn column, T value) {
        return (JoiningQueryWhere) super.add(valid, column, value);
    }

    @Override
    public JoiningQueryWhere add(boolean valid, TableColumn column, WhereOperator operator) {
        return (JoiningQueryWhere) super.add(valid, column, operator);
    }

    @Override
    public JoiningQueryWhere add(boolean valid, TableColumn column, Object value, WhereOperator operator) {
        return (JoiningQueryWhere) super.add(valid, column, value, operator);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere add(boolean valid, TableColumn column, T value, Condition condition) {
        return (JoiningQueryWhere) super.add(valid, column, value, condition);
    }

    @Override
    public JoiningQueryWhere add(boolean valid, TableColumn column, Object value, WhereOperator operator, Condition condition) {
        return (JoiningQueryWhere) super.add(valid, column, value, operator, condition);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andNotEqual(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andNotEqual(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andLike(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andLike(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andLeftLike(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andLeftLike(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andRightLike(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andRightLike(column, value);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T extends Iterable> JoiningQueryWhere andNotIn(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andNotIn(column, value);
    }

    @Override
    public JoiningQueryWhere andNotIn(TableColumn column, InlineSubQuery value) {
        return (JoiningQueryWhere) super.andNotIn(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orNotEqual(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orNotEqual(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orLike(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orLike(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orLeftLike(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orLeftLike(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orRightLike(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orRightLike(column, value);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T extends Iterable> JoiningQueryWhere orNotIn(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orNotIn(column, value);
    }

    @Override
    public JoiningQueryWhere orNotIn(TableColumn column, InlineSubQuery value) {
        return (JoiningQueryWhere) super.orNotIn(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andGreaterThan(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andGreaterThan(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andLessThan(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andLessThan(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orGreaterThan(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orGreaterThan(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orLessThan(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orLessThan(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andEqualOrGreaterThan(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andEqualOrGreaterThan(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere andEqualOrLessThan(TableColumn column, T value) {
        return (JoiningQueryWhere) super.andEqualOrLessThan(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orEqualOrGreaterThan(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orEqualOrGreaterThan(column, value);
    }

    @Override
    public <T extends Serializable> JoiningQueryWhere orEqualOrLessThan(TableColumn column, T value) {
        return (JoiningQueryWhere) super.orEqualOrLessThan(column, value);
    }
}