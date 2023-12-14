package org.devops.data.xjpa.table;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.devops.data.xjpa.constant.XjpaConstant;
import org.devops.data.xjpa.util.EntityUtil;
import org.devops.data.xjpa.util.NameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
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
public class EntityTable<K, V> implements TableMetadata {

    private final Logger logger = LoggerFactory.getLogger(EntityTable.class);

    private static final String AUTOINCREMENT = "auto_increment";

    private final String tableName;

    private final Class<V> entityType;

    private final Class<K> keyType;

    private final TableFieldContainer tableFieldContainer;

    /**
     * @param tableName
     * @param entityType
     * @param keyType
     * @param tableFieldContainer
     */
    public EntityTable(String tableName, Class<V> entityType, Class<K> keyType, TableFieldContainer tableFieldContainer) {
        this.tableName = tableName;
        this.entityType = entityType;
        this.keyType = keyType;
        this.tableFieldContainer = tableFieldContainer;
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
                    boolean isPriKey = tableField.getKey().equals(XjpaConstant.PRI_KEY);
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
     * @param entity
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public K getPrimaryKeyFieldValue(V entity) {
        EntityTableField entityTableField = getPrimaryKeyField();
        Field field = entityTableField.getJavaField();

        Object value = BeanUtil.getFieldValue(entity, field.getName());
        if (value != null) {
            return (K) value;
        }

        try {
            field.setAccessible(true);
            return (K) field.get(entity);
        } catch (IllegalAccessException e) {
            logger.error("get primaryKeyFieldValue error", e);
            return null;
        }finally {
            field.setAccessible(false);
        }
    }

    /**
     * @return 是否为自增主键
     */
    public boolean isAutoincrement() {
        EntityTableField primaryKeyField = getPrimaryKeyField();
        return primaryKeyField != null && primaryKeyField.getTableFieldMetadata().getExtra().contains(AUTOINCREMENT);
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
        return tableFieldContainer.get();
    }

    @Override
    public String toString() {
        return "EntityTable{" +
                "tableName='" + tableName + '\'' +
                ", entityType=" + entityType +
                ", keyType=" + keyType +
                ", tableFieldContainer=" + tableFieldContainer +
                '}';
    }


    public String getTableName() {
        return tableName;
    }

    public Class<V> getEntityType() {
        return entityType;
    }

    public Class<K> getKeyType() {
        return keyType;
    }

    public TableFieldContainer getTableFieldContainer() {
        return tableFieldContainer;
    }
}
