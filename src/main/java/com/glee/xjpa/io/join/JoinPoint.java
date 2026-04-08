package com.glee.xjpa.io.join;

import com.glee.xjpa.io.column.TableColumn;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description 连接点
 */
@SuppressWarnings("rawtypes")
public interface JoinPoint {

    Class getEntityType();

    String getTableName();

    String getTableAlias();

    TableColumn columnDef(String columnName);

    boolean isSoftDeleteEnabled();

}
