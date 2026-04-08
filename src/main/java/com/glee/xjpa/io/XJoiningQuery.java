package com.glee.xjpa.io;

import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.io.groupby.JoiningGroupBy;
import com.glee.xjpa.io.include.JoiningIncludeBy;
import com.glee.xjpa.io.orderby.JoiningOrderBy;
import com.glee.xjpa.sql.where.usermodel.QueryWhereModel;

/**
 * @author GENSEN
 * @date 2026/3/25
 * @description
 */
public interface XJoiningQuery<Column extends TableColumn> {

    XJoiningQuery<Column> where(QueryWhereModel<Column> where);

    XJoiningQuery<Column> limit(int start, int size);

    XJoiningQuery<Column> distinct();

    XJoiningQuery<Column> orderBy(JoiningOrderBy orderBy);

    XJoiningQuery<Column> include(JoiningIncludeBy including);

    XJoiningGroupByQuery<Column> groupBy(JoiningGroupBy groupBy);

}
