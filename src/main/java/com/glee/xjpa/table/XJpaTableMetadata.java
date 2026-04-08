package com.glee.xjpa.table;

import cn.hutool.core.collection.CollectionUtil;
import com.glee.xjpa.constant.XJpaConstant;
import com.glee.xjpa.exception.XJpaException;
import com.glee.xjpa.util.EntityUtil;
import com.glee.xjpa.util.NameUtil;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 实体类对应的表信息
 */
public class XJpaTableMetadata<K, E> implements TableMetadata {


    private static final String AUTOINCREMENT = "auto_increment";

    private final String tableName;

    private final Class<E> entityType;

    private final Class<K> keyType;

    private final TableFieldProvider tableFieldProvider;

    /**
     * @param tableName
     * @param entityType
     * @param keyType
     * @param tableFieldProvider
     */
    public XJpaTableMetadata(String tableName, Class<E> entityType, Class<K> keyType, TableFieldProvider tableFieldProvider) {
        this.tableName = tableName;
        this.entityType = entityType;
        this.keyType = keyType;
        this.tableFieldProvider = tableFieldProvider;
    }


    /**
     * @return 主键字段
     */
    public EntityTableField getPrimaryKeyField() {
        return getEntityTableFieldList()
                .stream()
                .filter(EntityTableField::isPriKey)
                .findFirst()
                .orElse(null);
    }

    public String getPrimaryKeyColumnName() {
        EntityTableField primaryKeyField = getPrimaryKeyField();
        if (primaryKeyField == null) {
            throw new XJpaException("primary key not found");
        }
        return primaryKeyField.column().name();
    }

    /**
     * @return
     */
    public List<EntityTableField> getEntityTableFieldList() {
        Map<String, Field> fieldMap = EntityUtil.getFields(entityType)
                .stream()
                .collect(Collectors.toMap(Field::getName, Function.identity()));
        return tableFields()
                .stream()
                .filter(tableField -> fieldMap.containsKey(tableField.getField()) || fieldMap.containsKey(NameUtil.toLHCase(tableField.getField())))
                .map(tableField -> {
                    Field field = fieldMap.getOrDefault(tableField.getField(), fieldMap.get(NameUtil.toLHCase(tableField.getField())));
                    Column column = field.getAnnotation(Column.class);
                    GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                    boolean isPriKey = tableField.getKey().equals(XJpaConstant.PRI_KEY);
                    return new EntityTableField(isPriKey, tableField, field, column, generatedValue);
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取一列，主键优先
     *
     * @return
     */
    public EntityTableField pickTableField() {
        EntityTableField primaryKeyField = getPrimaryKeyField();
        if (primaryKeyField != null) {
            return primaryKeyField;
        }
        return CollectionUtil.getFirst(getEntityTableFieldList());
    }


    /**
     * @return 是否为自增主键
     */
    public boolean isAutoincrement() {
        EntityTableField primaryKeyField = getPrimaryKeyField();
        return primaryKeyField != null && primaryKeyField.tableFieldMetadata().getExtra().contains(AUTOINCREMENT);
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public Class<?> entityType() {
        return entityType;
    }

    @Override
    public Class<?> keyType() {
        return keyType;
    }

    @Override
    public List<TableFieldMetadata> tableFields() {
        return tableFieldProvider.get();
    }


    public String getTableName() {
        return tableName;
    }

    public Class<E> getEntityType() {
        return entityType;
    }

    public Class<K> getKeyType() {
        return keyType;
    }

}
