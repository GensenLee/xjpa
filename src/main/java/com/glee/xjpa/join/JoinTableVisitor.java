package com.glee.xjpa.join;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 连接对象浏览
 */
public interface JoinTableVisitor {

    void visit(AbstractJoinModel joinModel);

}
