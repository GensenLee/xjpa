package com.glee.xjpa.sql.where.objects;

import com.glee.xjpa.sql.where.operate.WhereOperator;

/**
 * @author GENSEN
 * @date 2022/11/22
 * @description 单节点
 */
public interface IQueryWhereNode extends IQueryWhereObject {

    Object getColumn();

    Object getValue();

    WhereOperator getOperator();

    @Override
    default void accept(IQueryWhereObjectVisitor visitor) {
        if (visitor == null) {
            return;
        }
        visitor.visit(this);
    }
}
