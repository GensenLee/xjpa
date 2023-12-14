package org.devops.data.xjpa.sql.executor.result.reader;

import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 操作结果
 */
@Slf4j
public class ResultSetResult implements Result {

    /**
     * 使用map承接的结果
     */
    private final List<Map<String, Object>> rawMapTypeResult;

    public ResultSetResult(List<Map<String, Object>> rawMapTypeResult) {
        this.rawMapTypeResult = rawMapTypeResult;
    }

    /**
     * @param resultSet
     */
    public static Result read(ResultSet resultSet) {
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
            log.error("read result error", e);
        }
        return new ResultSetResult(rawMapTypeResult);
    }


    @Override
    public int getAffectRow() {
        return rawMapTypeResult.size();
    }

    @Override
    public List<Map<String, Object>> getRawResults() {
        return rawMapTypeResult;
    }
}
