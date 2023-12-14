package org.devops.data.xjpa.table;

import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/19
 * @description 表元数据
 */
public interface TableMetadata {

    String tableName();

    Class<?> entityType();

    Class<?> keyType();

    List<TableFieldMetadata> tableFields();

}
