package com.glee.xjpa.repository;

import com.glee.xjpa.repository.impl.DefaultUpdateRequest;
import com.glee.xjpa.sql.executor.UpdateColumn;

import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/1
 * @description 更新请求
 */
public interface UpdateRequest {

    /**
     * @return 更新列
     */
    List<UpdateColumn> getUpdateColumns();

    UpdateRequest add(UpdateColumn updateColumn);

    UpdateRequest add(String targetColumn, String operatorColumn, UpdateOperator updateOperator, Object value);

    default UpdateRequest add(String targetColumn, UpdateOperator updateOperator, Object value) {
        return add(targetColumn, null, updateOperator, value);
    }

    default UpdateRequest add(String targetColumn, Object value) {
        if (value == null) {
            return setNull(targetColumn);
        }
        return add(targetColumn, UpdateOperator.EQ, value);
    }

    UpdateRequest setNull(String targetColumn);

    /**
     * @return 构造器
     */
    static UpdateRequest def() {
        return new DefaultUpdateRequest();
    }

}
