package com.glee.xjpa.sql.where.objects;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 访问者
 */
public interface IQueryWhereObjectVisitor {

    void visit(IQueryWhereNodes node);
    void visit(IQueryWhereNode node);
    void visit(IQueryWhereString node);

}
