package com.glee.xjpa.sql.logger;

import com.glee.xjpa.lifecycle.Disposable;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/18
 * @description 可切换的logger
 */
public class SwitchableSqlLogger extends DefaultSqlLogger implements SwitchableLogger, Disposable {
    
    private final ThreadLocal<Boolean> closeStatus = InheritableThreadLocal.withInitial(() -> false);

    @Override
    public void logSql(String sql, Map<Integer, Object> parameters) {
        if (closeStatus.get()) {
            return;
        }
        super.logSql(sql, parameters);
    }

    @Override
    public void logSql(String sql) {
        if (closeStatus.get()) {
            return;
        }
        super.logSql(sql);
    }

    @Override
    public void logSql(String sql, List<Map<Integer, Object>> parameters) {
        if (closeStatus.get()) {
            return;
        }
        super.logSql(sql, parameters);
    }

    @Override
    public void logAffect(int affect, long start, long end) {
        if (closeStatus.get()) {
            return;
        }
        super.logAffect(affect, start, end);
    }

    @Override
    public void logResult(ResultSet resultSet, long start, long end) {
        if (closeStatus.get()) {
            return;
        }
        super.logResult(resultSet, start, end);
    }

    @Override
    public void closeLog(){
        closeStatus.set(true);
    }

    @Override
    public void openLog(){
        closeStatus.set(false);
    }

    @Override
    public void dispose() {
        openLog();
    }
}
