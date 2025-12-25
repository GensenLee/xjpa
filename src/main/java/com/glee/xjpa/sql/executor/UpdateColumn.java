package com.glee.xjpa.sql.executor;

import com.glee.xjpa.repository.UpdateOperator;


public class UpdateColumn {
    /**
     * 目标更新列
     */
    private final String targetColumn;

    private final UpdateOperator updateOperator;

    private final String operatorColumn;

    private final Object value;

    public UpdateColumn(String targetColumn, String operatorColumn, UpdateOperator updateOperator, Object value) {
        this.targetColumn = targetColumn;
        this.updateOperator = updateOperator;
        this.operatorColumn = operatorColumn;
        this.value = value;
    }


    public String getTargetColumn() {
        return targetColumn;
    }

    public UpdateOperator getUpdateOperator() {
        return updateOperator;
    }

    public String getOperatorColumn() {
        return operatorColumn;
    }

    public Object getValue() {
        return value;
    }
}