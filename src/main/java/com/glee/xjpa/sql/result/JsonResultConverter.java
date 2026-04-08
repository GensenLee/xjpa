package com.glee.xjpa.sql.result;

import com.alibaba.fastjson.JSON;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2026/4/1
 * @description
 */
public class JsonResultConverter<T> implements ResultConverter<T> {

    private final MapListResultConverter mapListResultConverter;

    private final Class<T> resultType;

    public JsonResultConverter(Class<T> resultType, MapListResultConverter mapListResultConverter) {
        this.mapListResultConverter = mapListResultConverter;
        this.resultType = resultType;
    }


    @Override
    public List<T> convert(ResultSet resultSet) {
        List<Map<String, Object>> rawResult = mapListResultConverter.convert(resultSet);
        return JSON.parseArray(JSON.toJSONString(rawResult), resultType);
    }
}
