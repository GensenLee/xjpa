package com.glee.xjpa.sql.executor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author GENSEN
 * @date 2022/11/1
 * @description update值
 */
public interface UpdateValueHandler {

    /**
     * @return 更新的列
     */
    List<String> updateColumnList();

    /**
     * @return 更新set参数
     */
    Map<Integer, Object> updateValues();

    /**
     * 定义set短语
     * @param targetColumn
     * @return
     */
    default String defineSetPhrase(String targetColumn){
        return String.format("`%s` = ?", targetColumn);
    }

}
