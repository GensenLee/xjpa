package org.devops.data.xjpa.sql.where.usermodel;

import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;

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

    IQueryWhereAcceptor<Column> andEqual(Column column, Object value);

    IQueryWhereAcceptor<Column> andIn(Column column, Object value);

    IQueryWhereAcceptor<Column> andBetween(Column column, Object start, Object end);

    IQueryWhereAcceptor<Column> andNotBetween(Column column, Object start, Object end);

    IQueryWhereAcceptor<Column> andNotNull(Column column);

    IQueryWhereAcceptor<Column> andIsNull(Column column);

    IQueryWhereAcceptor<Column> and(Column column, WhereOperator operator);

    IQueryWhereAcceptor<Column> and(Column column, Object value, WhereOperator operator);

    IQueryWhereAcceptor<Column> orEqual(Column column, Object value);

    IQueryWhereAcceptor<Column> orIn(Column column, Object value);

    IQueryWhereAcceptor<Column> orBetween(Column column, Object start, Object end);

    IQueryWhereAcceptor<Column> orNotBetween(Column column, Object start, Object end);

    IQueryWhereAcceptor<Column> orNotNull(Column column);

    IQueryWhereAcceptor<Column> orIsNull(Column column);

    IQueryWhereAcceptor<Column> or(Column column, WhereOperator operator);

    IQueryWhereAcceptor<Column> or(Column column, Object value, WhereOperator operator);

    IQueryWhereAcceptor<Column> equal(Column column, Object value, Condition condition);

    IQueryWhereAcceptor<Column> add(Column column, Object value, WhereOperator operator, Condition condition);
}
