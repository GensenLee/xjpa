package org.devops.data.xjpa.sql.executor;

import cn.hutool.core.util.StrUtil;
import org.devops.data.xjpa.repository.UpdateOperator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/8
 * @description 单列更新
 */
public class SingleColumnUpdateValueHandler implements UpdateValueHandler {

    /**
     * 目标更新列
     */
    private final String targetColumn;

    private final UpdateOperator updateOperator;

    private final String operatorColumn;

    private final Object value;

    private SingleColumnUpdateValueHandler(String targetColumn, UpdateOperator updateOperator, String operatorColumn, Object value) {
        this.targetColumn = targetColumn;
        this.updateOperator = updateOperator;
        this.operatorColumn = operatorColumn;
        this.value = value;
    }

    @Override
    public List<String> updateColumnList() {
        return Collections.singletonList(targetColumn);
    }

    @Override
    public Map<Integer, Object> updateValues() {
        return Collections.singletonMap(1, value);
    }


    public UpdateOperator getUpdateOperator() {
        return updateOperator != null ? updateOperator : UpdateOperator.EQ;
    }

    @Override
    public String defineSetPhrase(String targetColumn) {
        switch (getUpdateOperator()) {
            case ADD:case SUB:case MCL:case DIV:
                String optColumn = StrUtil.isNotEmpty(operatorColumn) ? operatorColumn : targetColumn;
                return String.format("`%s` = (`%s` %s ?)", targetColumn, optColumn, updateOperator.getOperator());
            default:
                return UpdateValueHandler.super.defineSetPhrase(targetColumn);
        }
    }

    public String getTargetColumn() {
        return targetColumn;
    }

    public String getOperatorColumn() {
        return operatorColumn;
    }

    public Object getValue() {
        return value;
    }

    public static SingleColumnUpdateValueHandlerBuilder builder() {
        return new SingleColumnUpdateValueHandlerBuilder();
    }

    public static final class SingleColumnUpdateValueHandlerBuilder {
        private String targetColumn;
        private UpdateOperator updateOperator;
        private String operatorColumn;
        private Object value;

        private SingleColumnUpdateValueHandlerBuilder() {
        }

        public static SingleColumnUpdateValueHandlerBuilder aSingleColumnUpdateValueHandler() {
            return new SingleColumnUpdateValueHandlerBuilder();
        }

        public SingleColumnUpdateValueHandlerBuilder targetColumn(String targetColumn) {
            this.targetColumn = targetColumn;
            return this;
        }

        public SingleColumnUpdateValueHandlerBuilder updateOperator(UpdateOperator updateOperator) {
            this.updateOperator = updateOperator;
            return this;
        }

        public SingleColumnUpdateValueHandlerBuilder operatorColumn(String operatorColumn) {
            this.operatorColumn = operatorColumn;
            return this;
        }

        public SingleColumnUpdateValueHandlerBuilder value(Object value) {
            this.value = value;
            return this;
        }

        public SingleColumnUpdateValueHandler build() {
            return new SingleColumnUpdateValueHandler(targetColumn, updateOperator, operatorColumn, value);
        }
    }
}
