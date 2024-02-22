package org.devops.data.xjpa.repository.impl;

import cn.hutool.core.util.StrUtil;
import org.devops.data.xjpa.repository.UpdateOperator;
import org.devops.data.xjpa.repository.UpdateRequest;
import org.devops.data.xjpa.sql.executor.UpdateColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/11
 * @description 默认
 */
public class DefaultUpdateRequest implements UpdateRequest {

    private final List<UpdateColumn> updateColumnList;

    public DefaultUpdateRequest(List<UpdateColumn> updateColumnList) {
        this.updateColumnList = updateColumnList;
    }

    public DefaultUpdateRequest() {
        this.updateColumnList = new ArrayList<>();
    }

    @Override
    public List<UpdateColumn> getUpdateColumns() {
        return updateColumnList;
    }

    @Override
    public UpdateRequest add(UpdateColumn updateColumn) {
        if (StrUtil.isEmpty(updateColumn.getTargetColumn())) {
            throw new IllegalArgumentException("targetColumn = " + updateColumn.getTargetColumn());
        }
        if (updateColumn.getValue() == null) {
            throw new IllegalArgumentException("value = " + updateColumn.getValue());
        }
        updateColumnList.add(updateColumn);
        return this;
    }

    @Override
    public UpdateRequest add(String targetColumn, String operatorColumn, UpdateOperator updateOperator, Object value) {
        updateColumnList.add(new UpdateColumn(targetColumn, operatorColumn, updateOperator, value));
        return this;
    }

    @Override
    public UpdateRequest setNull(String targetColumn) {
        updateColumnList.add(new UpdateColumn(targetColumn, null, UpdateOperator.SET_NULL, null));
        return this;
    }
}
