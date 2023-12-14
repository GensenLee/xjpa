package org.devops.data.xjpa.table;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import java.lang.reflect.Field;

/**
 * @author GENSEN
 * @date 2022/11/18
 * @description
 */
public class EntityTableField {

    /**
     * 是否为主键
     */
    private final boolean isPriKey;

    private final TableFieldMetadata tableFieldMetadata;
    private final Field javaField;
    private final Column column;

    private final GeneratedValue generatedValue;

    public EntityTableField(boolean isPriKey, TableFieldMetadata tableFieldMetadata, Field javaField, Column column,
                            GeneratedValue generatedValue) {
        this.isPriKey = isPriKey;
        this.tableFieldMetadata = tableFieldMetadata;
        this.javaField = javaField;
        this.column = column;
        this.generatedValue = generatedValue;
    }


    public boolean isPriKey() {
        return isPriKey;
    }

    public TableFieldMetadata getTableFieldMetadata() {
        return tableFieldMetadata;
    }

    public Field getJavaField() {
        return javaField;
    }

    public Column getColumn() {
        return column;
    }

    public GeneratedValue getGeneratedValue() {
        return generatedValue;
    }
}
