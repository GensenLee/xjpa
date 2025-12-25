package com.glee.xjpa.sql.where.handler;

import com.glee.xjpa.sql.where.QueryWhereUtil;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 默认
 */
public class DefaultQueryWhereHandler extends AbstractQueryWhereHandler {

    public DefaultQueryWhereHandler(IQueryWhereObject whereValue) {
        super(whereValue);
    }

    @Override
    public String toWhereString() {
        return QueryWhereUtil.toWhereString(whereValue);
    }
}
