package com.glee.xjpa.table;

import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description TableField容器
 */
public interface TableFieldProvider {

    List<TableFieldMetadata> get();

    void load();

}
