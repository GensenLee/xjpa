package org.devops.data.xjpa.sql.logger;

import lombok.extern.slf4j.Slf4j;
import org.devops.data.xjpa.util.PstParameter;
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
@Slf4j
public class DefaultSqlLogger implements SqlLogger{

    protected final Logger logger = LoggerFactory.getLogger("org.devops.data.SQL");

    @Override
    public void logSql(String sql, Map<Integer, Object> parameters) {
        for (Object value : parameters.values()) {
            sql = sql.replaceFirst("\\?", "'" + Matcher.quoteReplacement(valueToLongString(value)) + "'");
        }
        logger.info(sql);
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
        logger.info(sql);
    }

    @Override
    public void logSql(String sql, List<Map<Integer, Object>> parameters) {
        logger.info(sql);
        int index = 1;
        for (Map<Integer, Object> parameter : parameters) {

            List<Object> values = parameter.values().stream()
                    .map(val -> valueToLongString(val instanceof PstParameter ? ((PstParameter) val).getValue() : val))
                    .collect(Collectors.toList());

            logger.info("batch[{}] execute parameters [{}]", index++, values);
        }
    }

    @Override
    public void logAffect(int affect) {
        logger.info("affect row {}", affect);
    }

    @Override
    public void logResult(final ResultSet resultSet) {
        try {
            resultSet.last();
            logger.info("total row {}", resultSet.getRow());
        } catch (SQLException e) {
            log.error("logResult total error", e);
        }
    }
}
