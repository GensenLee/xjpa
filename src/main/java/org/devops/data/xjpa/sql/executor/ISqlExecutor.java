package org.devops.data.xjpa.sql.executor;

import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.result.reader.Result;

import java.sql.SQLException;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 执行器
 */
public interface ISqlExecutor<K, V> {

    Result execute(AbstractQueryRequest<K, V> query) throws SQLException;

}
