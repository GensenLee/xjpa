package com.glee.xjpa.table;

import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description Storage
 */
public class StorageTableFieldProvider implements TableFieldProvider {

    private final List<TableFieldMetadata> tableFieldList;

    public StorageTableFieldProvider(List<TableFieldMetadata> tableFieldList) {
        this.tableFieldList = tableFieldList;
    }

    @Override
    public List<TableFieldMetadata> get() {
        return tableFieldList;
    }

    @Override
    public void load() {
    }
}
