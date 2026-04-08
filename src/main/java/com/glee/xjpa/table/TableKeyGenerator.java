package com.glee.xjpa.table;

import com.glee.xjpa.table.identifier.IdentifierGenerator;
import com.glee.xjpa.table.identifier.IdentifierGeneratorType;
import com.glee.xjpa.table.identifier.TimestampIdentifier;
import com.glee.xjpa.table.identifier.UlidIdentifierGenerator;
import jakarta.persistence.GeneratedValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2026/3/30
 * @description 表主键生成器
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TableKeyGenerator {

    private static final Map<TableProperties<?, ?>, IdentifierGenerator<?>> GENERATORS = new HashMap<>();

    public static <K extends Serializable, E> K next(TableProperties<K, E> tableProperties) {
        IdentifierGenerator<?> identifierGenerator = GENERATORS.computeIfAbsent(tableProperties, TableKeyGenerator::createByProperties);
        return (K) identifierGenerator.next();
    }

    private static IdentifierGenerator createByProperties(TableProperties tableProperties) {
        IdentifierGenerator generator;
        XJpaTableMetadata metadata = tableProperties.getMetadata();
        EntityTableField primaryKeyField = metadata.getPrimaryKeyField();
        GeneratedValue generatedValue = primaryKeyField.generatedValue();
        if (generatedValue == null) {
            generator = createDefaultGenerator(primaryKeyField);
        } else {
            IdentifierGeneratorType instance = IdentifierGeneratorType.getInstance(generatedValue.generator());
            if (instance == null) {
                generator = createDefaultGenerator(primaryKeyField);
            } else {
                generator = instance.createIdentifierGenerator(tableProperties.getMetadata().tableName());
            }
        }

        return generator;
    }

    private static IdentifierGenerator createDefaultGenerator(EntityTableField primaryKeyField) {
        IdentifierGenerator generator;
        // 没有指定主键生成器时，使用默认值
        // 数值类型使用雪花ID，字符类型使用ulid
        Class<?> type = primaryKeyField.javaField().getType();
        if (type.isAssignableFrom(Number.class)) {
            generator = new TimestampIdentifier();
        } else {
            generator = new UlidIdentifierGenerator();
        }
        return generator;
    }


}
