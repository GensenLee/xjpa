package org.devops.data.xjpa.sql.where.usermodel;

import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2022/9/7
 * @description IModelWhere增强
 */
public interface IQueryWhereAcceptor<Column> {

    /**
         * 添加子条件
         * @param value
         * @return
         */
    IQueryWhereAcceptor<Column> put(IQueryWhereObject value);

    <T extends Serializable> IQueryWhereAcceptor<Column> andEqual(Column column, T value);

    <T extends Iterable> IQueryWhereAcceptor<Column> andIn(Column column, T value);

    <T extends Serializable> IQueryWhereAcceptor<Column> andBetween(Column column, T start, T end);

    <T extends Serializable> IQueryWhereAcceptor<Column> andNotBetween(Column column, T start, T end);

    IQueryWhereAcceptor<Column> andNotNull(Column column);

    IQueryWhereAcceptor<Column> andIsNull(Column column);

    IQueryWhereAcceptor<Column> and(Column column, WhereOperator operator);

    IQueryWhereAcceptor<Column> and(Column column, Object value, WhereOperator operator);

    <T extends Serializable> IQueryWhereAcceptor<Column> orEqual(Column column, T value);

    <T extends Iterable> IQueryWhereAcceptor<Column> orIn(Column column, T value);

    <T extends Serializable> IQueryWhereAcceptor<Column> orBetween(Column column, T start, T end);

    <T extends Serializable> IQueryWhereAcceptor<Column> orNotBetween(Column column, T start, T end);

    IQueryWhereAcceptor<Column> orNotNull(Column column);

    IQueryWhereAcceptor<Column> orIsNull(Column column);

    IQueryWhereAcceptor<Column> or(Column column, WhereOperator operator);

    IQueryWhereAcceptor<Column> or(Column column, Object value, WhereOperator operator);

    <T extends Serializable> IQueryWhereAcceptor<Column> equal(Column column, T value, Condition condition);

    IQueryWhereAcceptor<Column> add(Column column, Object value, WhereOperator operator, Condition condition);
}
