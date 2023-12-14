package org.devops.data.xjpa.sql.executor.query;

import lombok.Getter;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.table.EntityTableField;
import org.devops.data.xjpa.table.StorageTableFieldContainer;
import org.devops.data.xjpa.table.TableFieldMetadata;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 根据实体类更新
 */
@Getter
public class UpdateByEntityQueryRequest<K, V> extends AbstractQueryRequest<K, V> {

    final List<V> entityValues;

    public UpdateByEntityQueryRequest(RepositoryContext<K, V> context, List<V> entityValues) {
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
}
