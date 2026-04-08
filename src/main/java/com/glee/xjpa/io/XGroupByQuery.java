package com.glee.xjpa.io;

import com.glee.xjpa.io.having.GroupByHaving;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description
 */
public interface XGroupByQuery<Column> extends XQuery<Column> {

    XGroupByQuery<Column> having(GroupByHaving groupByHaving);


}
