package com.glee.xjpa.sql.executor.query;

import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.table.EntityTable;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.table.StorageTableFieldContainer;
import com.glee.xjpa.table.TableFieldMetadata;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 根据实体类更新
 */
public class UpdateByEntityQueryRequest<K, V> extends AbstractQueryRequest<K, V> {

    final Collection<V> entityValues;

    public UpdateByEntityQueryRequest(RepositoryContext<K, V> context, Collection<V> entityValues) {
        super(context);
        this.entityValues = entityValues;
    }

    public Set<String> includeColumns() {
        return null;
    }

    @Override
    public EntityTable<K, V> getEntityTable() {
        EntityTable<K, V> finalEntityTable = super.getEntityTable();

        Set<String> includeColumns = includeColumns();
        if (CollectionUtils.isEmpty(includeColumns)) {
            return finalEntityTable;
        }

        // 指定了更新列时需要过滤
        List<TableFieldMetadata> tableFieldList = context.getEntityTable().getEntityTableFieldList()
                .stream()
                .map(EntityTableField::getTableFieldMetadata)
                .filter(tableField -> includeColumns.contains(tableField.getField()))
                .collect(Collectors.toList());

        StorageTableFieldContainer storageTableFieldContainer = new StorageTableFieldContainer(tableFieldList);
        return new EntityTable<>(finalEntityTable.getTableName(), finalEntityTable.getEntityType(),
                finalEntityTable.getKeyType(), storageTableFieldContainer);
    }

    public List<V> getEntityValues() {
        return new ArrayList<>(entityValues);
    }
}
