package com.glee.xjpa.sql.executor;

import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.executor.result.reader.Result;

import java.sql.SQLException;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 执行器
 */
public interface ISqlExecutor<K, V> {

    Result execute(AbstractQueryRequest<K, V> query) throws SQLException;

}
