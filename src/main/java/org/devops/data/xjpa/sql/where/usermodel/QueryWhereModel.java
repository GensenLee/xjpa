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
    QueryWhereModel<Column> andEqual(Column column, Object value);

    @Override
    QueryWhereModel<Column> andIn(Column column, Object value);

    @Override
    QueryWhereModel<Column> and(Column column, WhereOperator operator);

    @Override
    QueryWhereModel<Column> and(Column column, Object value, WhereOperator operator);

    @Override
    QueryWhereModel<Column> orEqual(Column column, Object value);

    @Override
    QueryWhereModel<Column> orIn(Column column, Object value);

    @Override
    QueryWhereModel<Column> or(Column column, WhereOperator operator);

    @Override
    QueryWhereModel<Column> or(Column column, Object value, WhereOperator operator);

    @Override
    QueryWhereModel<Column> equal(Column column, Object value, Condition condition);

    @Override
    QueryWhereModel<Column> add(Column column, Object value, WhereOperator operator, Condition condition);
}
