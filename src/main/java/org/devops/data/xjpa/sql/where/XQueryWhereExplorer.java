package org.devops.data.xjpa.sql.where;

import org.devops.data.xjpa.sql.where.objects.IQueryWhereNode;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereNodes;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObjectVisitor;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereString;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 条件遍历
 */
public class XQueryWhereExplorer implements IQueryWhereObjectVisitor {

    protected final StringBuilder whereStringBuilder;

    protected final boolean explicit;

    public XQueryWhereExplorer() {
        this.whereStringBuilder = new StringBuilder();
        this.explicit = false;
    }

    public XQueryWhereExplorer(boolean explicit) {
        this.explicit = explicit;
        this.whereStringBuilder = new StringBuilder();
    }


    @Override
    public void visit(IQueryWhereNodes node) {
        if (node.isEmpty()) {
            return;
        }

        XQueryWhereExplorer childExplorer = new XQueryWhereExplorer(explicit);

        doAppend(node, childExplorer);
    }

    protected void doAppend(IQueryWhereNodes node, XQueryWhereExplorer childExplorer) {
        for (IQueryWhereObject child : node.children()) {
            if (child == null) {
                continue;
            }
            child.accept(childExplorer);
        }

        if (whereStringBuilder.length()  > 0) {
            whereStringBuilder.append(" ").append(node.attachCondition().getText()).append(" ");
        }
        whereStringBuilder.append("(").append(childExplorer.getWhereString()).append(")");
    }

    @Override
    public void visit(IQueryWhereNode node) {
        if (node.isEmpty()) {
            return;
        }

        if (whereStringBuilder.length() > 0) {
            whereStringBuilder.append(" ").append(node.attachCondition().getText()).append(" ");
        }

        whereStringBuilder.append(getColumnLabel(node)).append(" ");

        if (explicit) {
            whereStringBuilder.append(QueryWhereUtil.formatOperationExplicitString(node.getOperator(), node.getValue()));
        }else {
            whereStringBuilder.append(QueryWhereUtil.formatOperationString(node.getOperator(), node.getValue()));
        }
    }

    @Override
    public void visit(IQueryWhereString node) {
        if (node.isEmpty()) {
            return;
        }

        if (whereStringBuilder.length() > 0) {
            whereStringBuilder.append(" ").append(node.attachCondition().getText()).append(" ");
        }

        whereStringBuilder.append("(").append(node.getWhereString()).append(")");
    }

    public String getWhereString() {
        return whereStringBuilder.toString();
    }

    protected String getColumnLabel(IQueryWhereNode node) {
        return "`" + node.getColumn() + "`";
    }

}
