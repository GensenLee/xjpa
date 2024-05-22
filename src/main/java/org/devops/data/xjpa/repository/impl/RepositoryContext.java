package org.devops.data.xjpa.repository.impl;

import org.devops.data.xjpa.lifecycle.Closeable;
import org.devops.data.xjpa.lifecycle.Disposable;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.sql.result.parser.ResultParser;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.sql.where.XQueryWhere;

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
