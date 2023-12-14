package org.devops.data.xjpa.sql.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.core.utils.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/8
 * @description 多列更新
 */
@Slf4j
@Getter
public class MultipleColumnUpdateValueHandler implements UpdateValueHandler {


    private final Map<String, UpdateColumn> updateColumnMap;


    public MultipleColumnUpdateValueHandler() {
        this.updateColumnMap = new HashMap<>();
    }

    public MultipleColumnUpdateValueHandler(List<UpdateColumn> updateColumns){
        this();
        for (UpdateColumn updateColumn : updateColumns) {
            updateColumnMap.put(updateColumn.getTargetColumn(), updateColumn);
        }
    }


    @Override
    public List<String> updateColumnList() {
        return new ArrayList<>(updateColumnMap.keySet());
    }

    @Override
    public Map<Integer, Object> updateValues() {
        int index = 1;
        Map<Integer, Object> values = new HashMap<>();
        for (UpdateColumn updateColumn : updateColumnMap.values()) {
            values.put(index++, updateColumn.getValue());
        }
        return values;
    }

    @Override
    public String defineSetPhrase(String targetColumn) {
        UpdateColumn updateColumn = updateColumnMap.get(targetColumn);
        if (updateColumn == null) {
            throw new XjpaExecuteException("illegal parameter, targetColumn=" + targetColumn);
        }
        switch (updateColumn.getUpdateOperator()) {
            case ADD:case SUB:case MCL:case DIV:
                String optColumn = StringUtil.isNotEmpty(updateColumn.getOperatorColumn()) ? updateColumn.getOperatorColumn() : targetColumn;
                return String.format("`%s` = (`%s` %s ?)", targetColumn, optColumn, updateColumn.getUpdateOperator().getOperator());
            default:
                return UpdateValueHandler.super.defineSetPhrase(targetColumn);
        }
    }


}
