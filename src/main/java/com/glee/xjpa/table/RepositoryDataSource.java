package com.glee.xjpa.table;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description Repository数据源管理
 */
public interface RepositoryDataSource {

    default Connection getConnection() {
        return getConnection(false);
    }

    /**
     * @param closeTransaction 是否关闭事务
     * @return
     */
    Connection getConnection(boolean closeTransaction);

    /**
     * @return
     */
    DataSource getDataSource();

    void close();

}
