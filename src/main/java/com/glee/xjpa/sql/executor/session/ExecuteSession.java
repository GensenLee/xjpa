package com.glee.xjpa.sql.executor.session;

import com.glee.xjpa.lifecycle.Disposable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author GENSEN
 * @date 2022/11/7
 * @description 执行会话管理
 */
public interface ExecuteSession extends Disposable {

    /**
     * @param sql
     * @return
     */
    PreparedStatement readStatement(String sql);

    /**
     * @param sql
     * @return
     */
    PreparedStatement updateStatement(String sql);

    /**
     * 要求开启事务
     */
    void requireTransactionEnabled();

    /**
     * @param closeTransaction 是否关闭事务
     * @return
     */
    Connection getConnection(boolean closeTransaction);

    default Connection getConnection() {
        return getConnection(false);
    }

    /**
     * @return
     */
    DataSource getDataSource();
}
