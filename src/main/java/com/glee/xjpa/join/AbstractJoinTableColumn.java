package com.glee.xjpa.join;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description
 */
public abstract class AbstractJoinTableColumn implements TableColumn, JoinTableVisitor{

    protected String tableAlias;

    @Override
    public void visit(AbstractJoinModel joinModel) {
        this.tableAlias = joinModel == null ? getJoinTable().name().toLowerCase()
                : joinModel.getTableAlisa(getJoinTable());
    }

    abstract JoinTable getJoinTable();

}
