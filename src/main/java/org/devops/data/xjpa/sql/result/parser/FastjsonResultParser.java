package org.devops.data.xjpa.sql.result.parser;

import com.alibaba.fastjson.JSON;
import org.devops.core.utils.util.ListUtil;
import org.devops.core.utils.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 默认结果解析器
 *
 * @author GENSEN
 * @date 2022/11/8
 * @description 基于fastjson
 */
public class FastjsonResultParser implements ResultParser {
    @Override
    public <V> List<V> parse(List<Map<String, Object>> rawResult, Class<V> resultType) {
        return JSON.parseArray(JSON.toJSONString(rawResult), resultType);
    }

    @Override
    public <V> List<V> parseSingleColumn(List<Map<String, Object>> rawResult, String column, Class<V> resultType) {
        if (ListUtil.isNull(rawResult)) {
            return Collections.emptyList();
        }
        List<Object> columnValueList = rawResult.stream()
                .map(map -> map.get(column))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (StringUtil.isEmpty(columnValueList)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(JSON.toJSONString(columnValueList), resultType);
    }
}
