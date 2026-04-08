package com.glee.xjpa.io;

import com.glee.xjpa.io.groupby.GroupBy;
import com.glee.xjpa.io.include.IncludeBy;
import com.glee.xjpa.io.orderby.OrderBy;
import com.glee.xjpa.sql.where.usermodel.QueryWhereModel;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description
 */
public interface XQuery<Column> {

    XQuery<Column> where(QueryWhereModel<Column> where);

    XQuery<Column> limit(int start, int size);

    XQuery<Column> distinct();

    XQuery<Column> orderBy(OrderBy orderBy);

    XQuery<Column> include(IncludeBy includeBy);

    XGroupByQuery<Column> groupBy(GroupBy groupBy);

}
