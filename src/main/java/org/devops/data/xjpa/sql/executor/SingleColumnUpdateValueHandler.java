package org.devops.data.xjpa.sql.executor;

import lombok.Builder;
import lombok.Getter;
import org.devops.data.xjpa.repository.UpdateOperator;
import org.devops.core.utils.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/8
 * @description 单列更新
 */
@Getter
@Builder
public class SingleColumnUpdateValueHandler implements UpdateValueHandler {

    /**
     * 目标更新列
     */
    private final String targetColumn;

    private final UpdateOperator updateOperator;

    private final String operatorColumn;

    private final Object value;

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
                String optColumn = StringUtil.isNotEmpty(operatorColumn) ? operatorColumn : targetColumn;
                return String.format("`%s` = (`%s` %s ?)", targetColumn, optColumn, updateOperator.getOperator());
            default:
                return UpdateValueHandler.super.defineSetPhrase(targetColumn);
        }
    }
}
