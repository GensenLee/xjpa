package com.glee.xjpa.io;

import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.io.having.JoiningGroupByHave;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description
 */
public interface XJoiningGroupByQuery<Column extends TableColumn> extends XJoiningQuery<Column> {

    XJoiningGroupByQuery<Column> having(JoiningGroupByHave groupByHave);


}
