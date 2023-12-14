package org.devops.data.xjpa.sql.executor;

import lombok.Getter;
import org.devops.data.xjpa.repository.UpdateOperator;


@Getter
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
}