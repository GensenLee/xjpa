package com.glee.xjpa.util;

import com.glee.xjpa.sql.executor.AbstractSqlExecutor;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.executor.result.reader.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description SqlExecutor util
 */
public class SqlExecutorUtil {

    private static final Logger logger = LoggerFactory.getLogger(SqlExecutorUtil.class);

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Result execute(AbstractSqlExecutor sqlExecutor, AbstractQueryRequest queryRequest) throws SQLException {
        if (sqlExecutor == null || queryRequest == null) {
            return Result.Builder.build(0);
        }
        return sqlExecutor.execute(queryRequest);
    }

}
