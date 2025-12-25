package com.glee.xjpa.sql.logger;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description sql日志控制
 */
public interface SqlLogger {

    /**
     * @param sql
     * @param parameters
     */
    void logSql(String sql, Map<Integer, Object> parameters);

    void logSql(String sql);

    void logSql(String sql, List<Map<Integer, Object>> parameters);

    /**
     * @param affect
     */
    void logAffect(int affect, long start, long end);

    /**
     * @param resultSet
     */
    void logResult(ResultSet resultSet, long start, long end);

}
