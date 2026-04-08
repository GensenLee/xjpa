package com.glee.xjpa.sql.result;

import com.glee.xjpa.exception.XJpaExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2026/4/1
 * @description
 */
public class MapListResultConverter implements ResultConverter<Map<String, Object>> {
    private final static Logger log = LoggerFactory.getLogger(MapListResultConverter.class);


    @Override
    public List<Map<String, Object>> convert(ResultSet resultSet) {
        List<Map<String, Object>> rawMapTypeResult = new ArrayList<>();
        try {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (!resultSet.isBeforeFirst()) {
                resultSet.beforeFirst();
            }
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String fieldKey = metaData.getColumnLabel(i);
                    row.put(fieldKey, resultSet.getObject(i));
                }
                rawMapTypeResult.add(row);
            }

        } catch (SQLException e) {
            log.error("ResultSet read error", e);
            throw new XJpaExecuteException("result convert error", e);
        }
        return rawMapTypeResult;
    }
}
