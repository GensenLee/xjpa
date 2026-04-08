package com.glee.xjpa.sql;

import com.glee.xjpa.constant.XJpaConstant;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.table.TableProperties;
import com.glee.xjpa.table.XJpaTableMetadata;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2026/3/27
 * @description 插入sql语句
 */
public class InsertSqlTemplate {


    private final TableProperties<?, ?> tableProperties;


    public InsertSqlTemplate(TableProperties<?, ?> tableProperties) {
        this.tableProperties = tableProperties;
    }

    public String getInsertTemplate() {
        XJpaTableMetadata<?, ?> metadata = tableProperties.getMetadata();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into ")
                .append(metadata.getTableName());

        List<EntityTableField> tableFieldList = getEntityTableFieldsForInsertion(metadata);


        String joiningColumns = tableFieldList.stream()
                .map(tf -> tf.column().name())
                .collect(Collectors.joining(XJpaConstant.COMMA_MARK));

        String joiningPlaceholder = tableFieldList.stream()
                .map(tf -> "?")
                .collect(Collectors.joining(XJpaConstant.COMMA_MARK));

        sqlBuilder.append("(").append(joiningColumns).append(")")
                .append(" values (")
                .append(joiningPlaceholder)
                .append(")");

        return sqlBuilder.toString();
    }

    public static List<EntityTableField> getEntityTableFieldsForInsertion(XJpaTableMetadata<?, ?> metadata) {
        return metadata.getEntityTableFieldList()
                .stream()
                // 排序保证每次获取顺序一致
                .sorted(Comparator.comparing(o -> o.column().name()))
                .toList();
    }
}
