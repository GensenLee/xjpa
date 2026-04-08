package com.glee.xjpa.io.update;

import com.glee.xjpa.exception.XJpaException;
import com.glee.xjpa.io.SqlClause;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.table.TableProperties;
import com.glee.xjpa.util.NameUtil;
import com.glee.xjpa.util.PstParameter;

import java.util.List;

/**
 * @author GENSEN
 * @date 2026/3/31
 * @description update set子句
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class UpdateSetClause extends SqlClause {

    protected final String column;
    protected final UpdateSetBlock value;

    protected UpdateSetClause(String column, UpdateSetBlock value) {
        this.column = column;
        this.value = value;
    }

    @Override
    public String toSqlClause() {
        return column + "=" + value.toUpdateTemplateClause();
    }


    public PstParameter getParameter(TableProperties tableProperties) {
        List<EntityTableField> entityTableFieldList = tableProperties.getMetadata().getEntityTableFieldList();
        EntityTableField entityTableField = entityTableFieldList.stream()
                .filter(etf -> matchField(etf, column))
                .findFirst()
                .orElseThrow(() -> new XJpaException("set column not exist"));

        return new PstParameter(value.getParameter(), entityTableField);
    }

    private boolean matchField(EntityTableField etf, String column) {
        if (etf.column() != null) {
            return etf.column().name().equalsIgnoreCase(column);
        }

        String javaFieldName = etf.javaField().getName();
        return javaFieldName.equalsIgnoreCase(column) ||
                NameUtil.camelToSnake(javaFieldName).equalsIgnoreCase(column);
    }
}
