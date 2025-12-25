package com.glee.xjpa.join;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.sql.where.QueryWhereUtil;
import com.glee.xjpa.sql.where.XQueryWhereExplorer;
import com.glee.xjpa.sql.where.objects.IQueryWhereNode;
import com.glee.xjpa.sql.where.objects.IQueryWhereNodes;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 条件遍历
 */
public class JoinTableQueryWhereExplorer extends XQueryWhereExplorer {


    protected final AbstractJoinModel joinModel;

    public JoinTableQueryWhereExplorer(AbstractJoinModel joinModel) {
        super(true);
        this.joinModel = joinModel;
    }

    @Override
    public void visit(IQueryWhereNodes node) {
        if (node.isEmpty()) {
            return;
        }

        XQueryWhereExplorer childExplorer = new JoinTableQueryWhereExplorer(joinModel);

        doAppend(node, childExplorer);
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

        Object value = node.getValue();
        String columnLabel = getTypeIfMatch(value);

        if (StrUtil.isNotEmpty(columnLabel)) {
            value = columnLabel;
        }

        if (explicit) {
            whereStringBuilder.append(QueryWhereUtil.formatOperationExplicitString(node.getOperator(), value));
        }else {
            whereStringBuilder.append(QueryWhereUtil.formatOperationString(node.getOperator(), value));
        }

    }

    @Override
    protected String getColumnLabel(IQueryWhereNode node) {
        Object column = node.getColumn();
        String columnLabel = getTypeIfMatch(column);
        if (StrUtil.isNotEmpty(columnLabel)) {
            return columnLabel;
        }
        return super.getColumnLabel(node);
    }

    private String getTypeIfMatch(Object column) {
        if (column instanceof JoinTableVisitor) {
            ((JoinTableVisitor) column).visit(joinModel);
        }
        if (column instanceof TableColumn) {
            return ((TableColumn) column).getColumnLabel();
        }
        return null;
    }

}
