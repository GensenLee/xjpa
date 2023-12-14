package org.devops.data.xjpa.table;

import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description Storage
 */
public class StorageTableFieldContainer implements TableFieldContainer{

    private final List<TableFieldMetadata> tableFieldList;

    public StorageTableFieldContainer(List<TableFieldMetadata> tableFieldList) {
        this.tableFieldList = tableFieldList;
    }

    @Override
    public List<TableFieldMetadata> get() {
        return tableFieldList;
    }
}
