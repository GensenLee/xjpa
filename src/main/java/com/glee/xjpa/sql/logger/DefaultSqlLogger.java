package com.glee.xjpa.sql.logger;

import com.glee.xjpa.util.PstParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description 默认
 */
public class DefaultSqlLogger implements SqlLogger{

    protected static final Logger log = LoggerFactory.getLogger("XJpa.SQL");

    @Override
    public void logSql(String sql, Map<Integer, Object> parameters) {
        for (Object value : parameters.values()) {
            if (value instanceof Number n) {
                sql = sql.replaceFirst("\\?", String.valueOf(n));
            }else {
                sql = sql.replaceFirst("\\?", "'" + Matcher.quoteReplacement(valueToLongString(value)) + "'");
            }
        }
        log.info(sql);
    }

    protected String valueToLongString(Object value) {
        String strValue = String.valueOf(value);

        if (strValue.length() <= 5000) {
            return strValue;
        }

        return "<" + strValue.substring(0, 10) + "...long string omitted>";
    }

    @Override
    public void logSql(String sql) {
        log.info(sql);
    }

    @Override
    public void logSql(String sql, List<Map<Integer, Object>> parameters) {
        log.info(sql);
        int index = 1;
        for (Map<Integer, Object> parameter : parameters) {

            List<Object> values = parameter.values().stream()
                    .map(val -> valueToLongString(val instanceof PstParameter ? ((PstParameter) val).value() : val))
                    .collect(Collectors.toList());

            log.info("batch[{}] execute parameters [{}]", index++, values);
        }
    }

    @Override
    public void logAffect(int affect, long start, long end) {
        log.info("affect {} rows in {} ms", affect, end - start);
    }

    @Override
    public void logResult(final ResultSet resultSet, long start, long end) {
        try {
            resultSet.last();
            log.info("fetch {} rows in {} ms", resultSet.getRow(), end - start);
            resultSet.first();
        } catch (SQLException e) {
            log.error("logResult total error", e);
        } finally {
        }
    }
}
