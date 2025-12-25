package com.glee.xjpa.sql.where.usermodel;

import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import com.glee.xjpa.sql.where.operate.Condition;
import com.glee.xjpa.sql.where.operate.WhereOperator;
import com.glee.xjpa.sql.where.subquery.InlineSubQuery;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2023/6/23
 * @description
 */
public interface QueryWhereModel<Column> extends Serializable, IQueryWhereObject {

    @Override
    QueryWhereModel<Column> condition(Condition condition);

    QueryWhereModel<Column> put(IQueryWhereObject value);

    <T extends Serializable> QueryWhereModel<Column> andEqual(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> andNotEqual(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> andLike(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> andLeftLike(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> andRightLike(Column column, T value);

    @SuppressWarnings("rawtypes")
    <T extends Iterable> QueryWhereModel<Column> andIn(Column column, T value);

    @SuppressWarnings("rawtypes")
    <T extends Iterable> QueryWhereModel<Column> andNotIn(Column column, T value);

    QueryWhereModel<Column> andIn(Column column, InlineSubQuery value);

    QueryWhereModel<Column> andNotIn(Column column, InlineSubQuery value);

    <T extends Serializable> QueryWhereModel<Column> andBetween(Column column, T start, T end);

    <T extends Serializable> QueryWhereModel<Column> andNotBetween(Column column, T start, T end);

    QueryWhereModel<Column> andNotNull(Column column);

    QueryWhereModel<Column> andIsNull(Column column);

    <T extends Serializable> QueryWhereModel<Column> andGreaterThan(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> andLessThan(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> andEqualOrGreaterThan(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> andEqualOrLessThan(Column column, T value);

    QueryWhereModel<Column> and(Column column, WhereOperator operator);

    QueryWhereModel<Column> and(Column column, Object value, WhereOperator operator);

    <T extends Serializable> QueryWhereModel<Column> orEqual(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> orNotEqual(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> orLike(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> orLeftLike(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> orRightLike(Column column, T value);

    @SuppressWarnings("rawtypes")
    <T extends Iterable> QueryWhereModel<Column> orIn(Column column, T value);

    @SuppressWarnings("rawtypes")
    <T extends Iterable> QueryWhereModel<Column> orNotIn(Column column, T value);

    QueryWhereModel<Column> orIn(Column column, InlineSubQuery value);

    QueryWhereModel<Column> orNotIn(Column column, InlineSubQuery value);

    <T extends Serializable> QueryWhereModel<Column> orBetween(Column column, T start, T end);

    <T extends Serializable> QueryWhereModel<Column> orNotBetween(Column column, T start, T end);

    QueryWhereModel<Column> orNotNull(Column column);

    QueryWhereModel<Column> orIsNull(Column column);

    <T extends Serializable> QueryWhereModel<Column> orGreaterThan(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> orLessThan(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> orEqualOrGreaterThan(Column column, T value);

    <T extends Serializable> QueryWhereModel<Column> orEqualOrLessThan(Column column, T value);

    QueryWhereModel<Column> or(Column column, WhereOperator operator);

    QueryWhereModel<Column> or(Column column, Object value, WhereOperator operator);

    <T extends Serializable> QueryWhereModel<Column> equal(Column column, T value, Condition condition);

    QueryWhereModel<Column> add(Column column, Object value, WhereOperator operator, Condition condition);


}
