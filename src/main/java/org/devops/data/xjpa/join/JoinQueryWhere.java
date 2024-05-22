package org.devops.data.xjpa.join;

import org.devops.data.xjpa.sql.where.XQueryWhereExplorer;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;
import org.devops.data.xjpa.sql.where.subquery.InlineSubQuery;
import org.devops.data.xjpa.sql.where.usermodel.GenericQueryWhere;
import org.devops.data.xjpa.sql.where.usermodel.IQueryWhereAcceptor;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2023/6/23
 * @description 连表支持
 */
public class JoinQueryWhere extends GenericQueryWhere<TableColumn> {

    public JoinQueryWhere(IQueryWhereObject whereObject) {
        super(whereObject);
    }

    public JoinQueryWhere() {
        super();
    }

    public <T extends Serializable> JoinQueryWhere(TableColumn column, T value) {
        super(column, value);
    }

    public <T extends Serializable> JoinQueryWhere(TableColumn column, T value, WhereOperator operator) {
        super(column, value, operator);
    }

    public <T extends Serializable> JoinQueryWhere(TableColumn column, T value, WhereOperator operator, Condition condition) {
        super(column, value, operator, condition);
    }


    @Override
    public JoinQueryWhere put(IQueryWhereObject value) {
        return (JoinQueryWhere) super.put(value);
    }


    @Override
    public <T extends Serializable> JoinQueryWhere andEqual(TableColumn column, T value) {
        return (JoinQueryWhere) super.andEqual(column, value);
    }

    @Override
    public <T extends Iterable> JoinQueryWhere andIn(TableColumn column, T value) {
        return (JoinQueryWhere) super.andIn(column, value);
    }

    @Override
    public JoinQueryWhere andIn(TableColumn column, InlineSubQuery value) {
        return (JoinQueryWhere) super.and(column, value, WhereOperator.IN);
    }


    @Override
    public JoinQueryWhere and(TableColumn column, WhereOperator operator) {
        return (JoinQueryWhere) super.and(column, operator);
    }

    @Override
    public JoinQueryWhere and(TableColumn column, Object value, WhereOperator operator) {
        return (JoinQueryWhere) super.and(column, value, operator);
    }

    @Override
    public <T extends Serializable> JoinQueryWhere andBetween(TableColumn column, T start, T end) {
        return (JoinQueryWhere) super.andBetween(column, start, end);
    }

    @Override
    public <T extends Serializable> JoinQueryWhere andNotBetween(TableColumn column, T start, T end) {
        return (JoinQueryWhere) super.andNotBetween(column, start, end);
    }

    @Override
    public JoinQueryWhere andNotNull(TableColumn column) {
        return (JoinQueryWhere) super.andNotNull(column);
    }

    @Override
    public JoinQueryWhere andIsNull(TableColumn column) {
        return (JoinQueryWhere) super.andIsNull(column);
    }

    @Override
    public <T extends Serializable> JoinQueryWhere orBetween(TableColumn column, T start, T end) {
        return (JoinQueryWhere) super.orBetween(column, start, end);
    }

    @Override
    public <T extends Serializable> JoinQueryWhere orNotBetween(TableColumn column, T start, T end) {
        return (JoinQueryWhere) super.orNotBetween(column, start, end);
    }

    @Override
    public JoinQueryWhere orNotNull(TableColumn column) {
        return (JoinQueryWhere) super.orNotNull(column);
    }

    @Override
    public JoinQueryWhere orIsNull(TableColumn column) {
        return (JoinQueryWhere) super.orIsNull(column);
    }

    @Override
    public <T extends Serializable> JoinQueryWhere orEqual(TableColumn column, T value) {
        return (JoinQueryWhere) super.orEqual(column, value);
    }

    @Override
    public <T extends Iterable> JoinQueryWhere orIn(TableColumn column, T value) {
        return (JoinQueryWhere) super.orIn(column, value);
    }

    @Override
    public JoinQueryWhere or(TableColumn column, WhereOperator operator) {
        return (JoinQueryWhere) super.or(column, operator);
    }

    @Override
    public JoinQueryWhere or(TableColumn column, Object value, WhereOperator operator) {
        return (JoinQueryWhere) super.or(column, value, operator);
    }

    @Override
    public <T extends Serializable> JoinQueryWhere equal(TableColumn column, T value, Condition condition) {
        return (JoinQueryWhere) super.equal(column, value, condition);
    }

    @Override
    public JoinQueryWhere add(TableColumn column, Object value, WhereOperator operator, Condition condition) {
        return (JoinQueryWhere) super.add(column, value, operator, condition);
    }

    @Override
    public JoinQueryWhere add(boolean valid, IQueryWhereObject value) {
        return (JoinQueryWhere) super.add(valid, value);
    }

    @Override
    public <T extends Serializable> JoinQueryWhere add(boolean valid, TableColumn column, T value) {
        return (JoinQueryWhere) super.add(valid, column, value);
    }

    @Override
    public JoinQueryWhere add(boolean valid, TableColumn column, WhereOperator operator) {
        return (JoinQueryWhere) super.add(valid, column, operator);
    }

    @Override
    public JoinQueryWhere add(boolean valid, TableColumn column, Object value, WhereOperator operator) {
        return (JoinQueryWhere) super.add(valid, column, value, operator);
    }

    @Override
    public <T extends Serializable> JoinQueryWhere add(boolean valid, TableColumn column, T value, Condition condition) {
        return (JoinQueryWhere) super.add(valid, column, value, condition);
    }

    @Override
    public JoinQueryWhere add(boolean valid, TableColumn column, Object value,
                              WhereOperator operator, Condition condition) {
        return (JoinQueryWhere) super.add(valid, column, value, operator, condition);
    }


    @Override
    public JoinQueryWhere condition(Condition condition) {
        return (JoinQueryWhere) super.condition(condition);
    }


    @Override
    public String toString() {
        XQueryWhereExplorer xQueryWhereExplorer = new JoinTableQueryWhereExplorer(null);
        accept(xQueryWhereExplorer);
        return xQueryWhereExplorer.getWhereString();
    }
}
