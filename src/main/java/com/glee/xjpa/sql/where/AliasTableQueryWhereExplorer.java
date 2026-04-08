package com.glee.xjpa.sql.where;

import com.glee.xjpa.constant.XJpaConstant;
import com.glee.xjpa.sql.where.objects.IQueryWhereNode;
import com.glee.xjpa.sql.where.objects.IQueryWhereNodes;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 条件遍历
 */
public class AliasTableQueryWhereExplorer extends XQueryWhereExplorer {


    private final String tableAlias;


    public AliasTableQueryWhereExplorer(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public AliasTableQueryWhereExplorer(boolean explicit, String tableAlias) {
        super(explicit);
        this.tableAlias = tableAlias;
    }

    @Override
    public void visit(IQueryWhereNodes node) {
        if (node.isEmpty()) {
            return;
        }

        XQueryWhereExplorer childExplorer = new AliasTableQueryWhereExplorer(explicit, tableAlias);

        doAppend(node, childExplorer);
    }
    @Override
    protected String getColumnLabel(IQueryWhereNode node) {
        return tableAlias + XJpaConstant.CHAR_POINT + super.getColumnLabel(node);
    }
}
