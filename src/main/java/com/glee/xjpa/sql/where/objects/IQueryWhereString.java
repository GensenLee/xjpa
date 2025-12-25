package com.glee.xjpa.sql.where.objects;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 文本条件
 */
public interface IQueryWhereString extends IQueryWhereObject {
    String getWhereString();

    @Override
    default void accept(IQueryWhereObjectVisitor visitor) {
        if (visitor == null) {
            return;
        }
        visitor.visit(this);
    }
}
