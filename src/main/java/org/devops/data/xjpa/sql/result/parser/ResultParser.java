package org.devops.data.xjpa.sql.result.parser;

import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description 查询结果类型构造
 */
public interface ResultParser {

    <V> List<V> parse(List<Map<String, Object>> rawResult, Class<V> resultType);

    <V> List<V> parseSingleColumn(List<Map<String, Object>> rawResult, String column, Class<V> resultType);

}
