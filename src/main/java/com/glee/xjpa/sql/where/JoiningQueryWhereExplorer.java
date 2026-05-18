package com.glee.xjpa.sql.where;

import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.sql.where.objects.IQueryWhereNode;
import com.glee.xjpa.sql.where.objects.IQueryWhereNodes;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接查询 Where 条件遍历器
 */
public class JoiningQueryWhereExplorer extends XQueryWhereExplorer {

    public JoiningQueryWhereExplorer() {
        super();
    }

    public JoiningQueryWhereExplorer(boolean explicit) {
        super(explicit);
    }

    @Override
    public void visit(IQueryWhereNodes node) {
        if (node.isEmpty()) {
            return;
        }
        XQueryWhereExplorer childExplorer = new JoiningQueryWhereExplorer(explicit);
        doAppend(node, childExplorer);
    }

    @Override
    protected String getColumnLabel(IQueryWhereNode node) {
        if (node.getColumn() instanceof TableColumn) {
            return ((TableColumn) node.getColumn()).getColumnLabel();
        }
        return super.getColumnLabel(node);
    }
}
