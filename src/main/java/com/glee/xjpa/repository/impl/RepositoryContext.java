package com.glee.xjpa.repository.impl;

import com.glee.xjpa.sql.where.XQueryWhere;
import com.glee.xjpa.lifecycle.Closeable;
import com.glee.xjpa.lifecycle.Disposable;
import com.glee.xjpa.sql.executor.session.ExecuteSession;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.sql.result.parser.ResultParser;
import com.glee.xjpa.table.EntityTable;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description 数据源获取接口
 */
public interface RepositoryContext<K, V> extends Disposable, Closeable, RepositoryContextObservable, RepositoryContextAttribute {


    SqlLogger localSqlLogger();

    XQueryWhere localQueryWhere();

    ExecuteSession localSessionManager();

    EntityTable<K, V> getEntityTable();

    ResultParser getResultParser();
}
