package com.glee.xjpa.sql.where.usermodel;

import com.glee.xjpa.sql.where.AliasTableQueryWhereExplorer;
import com.glee.xjpa.sql.where.XQueryWhereExplorer;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import com.glee.xjpa.sql.where.operate.Condition;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 别名支持
 */
public class AliasTableQueryWhere extends QueryWhere {

    private final String tableAlias;


    public AliasTableQueryWhere(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    @Override
    public String toString() {
        XQueryWhereExplorer xQueryWhereExplorer = new AliasTableQueryWhereExplorer(true, tableAlias);
        accept(xQueryWhereExplorer);
        return xQueryWhereExplorer.getWhereString();
    }

    public Condition getCondition() {
        return condition;
    }

    @Override
    public QueryWhere put(IQueryWhereObject value) {
        AliasTableQueryWhere where = new AliasTableQueryWhere(tableAlias);
        where.whereObject = value;
        return super.put(where);
    }
}
