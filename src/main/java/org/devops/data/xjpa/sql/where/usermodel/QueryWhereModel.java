package org.devops.data.xjpa.sql.where.usermodel;

import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2023/6/23
 * @description
 */
public interface QueryWhereModel<Column> extends Serializable, IQueryWhereObject, IQueryWhereAcceptor<Column>{

    @Override
    QueryWhereModel<Column> condition(Condition condition);

    @Override
    QueryWhereModel<Column> put(IQueryWhereObject value);

    @Override
    <T extends Serializable> QueryWhereModel<Column> andEqual(Column column, T value);

    @Override
    <T extends Iterable> QueryWhereModel<Column> andIn(Column column, T value);

    @Override
    QueryWhereModel<Column> and(Column column, WhereOperator operator);

    @Override
    QueryWhereModel<Column> and(Column column, Object value, WhereOperator operator);

    @Override
    <T extends Serializable> QueryWhereModel<Column> orEqual(Column column, T value);

    @Override
    <T extends Iterable> QueryWhereModel<Column> orIn(Column column, T value);

    @Override
    QueryWhereModel<Column> or(Column column, WhereOperator operator);

    @Override
    QueryWhereModel<Column> or(Column column, Object value, WhereOperator operator);

    @Override
    <T extends Serializable> QueryWhereModel<Column> equal(Column column, T value, Condition condition);

    @Override
    QueryWhereModel<Column> add(Column column, Object value, WhereOperator operator, Condition condition);
}
