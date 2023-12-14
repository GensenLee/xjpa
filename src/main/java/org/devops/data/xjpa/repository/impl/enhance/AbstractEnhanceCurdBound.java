package org.devops.data.xjpa.repository.impl.enhance;

import org.devops.core.utils.util.IntUtil;
import org.devops.core.utils.util.StringUtil;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.executor.DefaultSortHandler;
import org.devops.data.xjpa.sql.executor.LimitHandler;
import org.devops.data.xjpa.sql.executor.SortHandler;
import org.devops.data.xjpa.sql.executor.SortType;
import org.devops.data.xjpa.table.EntityTableField;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description
 */
public abstract class AbstractEnhanceCurdBound<K extends Serializable, V> implements EnhanceCurdBound<K, V> {
    protected final RepositoryContext<K, V> context;

    protected AbstractEnhanceCurdBound(RepositoryContext<K, V> context) {
        this.context = context;
    }


    protected SortHandler createSortHandler(String sortString, List<OrderParameter> orderParameters) {
        if (StringUtil.isNotEmpty(sortString)) {
            return new DefaultSortHandler(Collections.singletonList(new SortHandler.SortSet(
                    sortString, SortType.plain
            )));
        }
        if (orderParameters.isEmpty()) {
            return SortHandler.empty();
        }
        List<SortHandler.SortSet> sortSetList = orderParameters.stream()
                .map(sort -> new SortHandler.SortSet(sort.column, sort.sortType))
                .distinct()
                .collect(Collectors.toList());

        return new DefaultSortHandler(sortSetList);
    }

    protected LimitHandler createLimitHandler(LimitParameter limitParameter) {
        if (limitParameter == null || limitParameter.start == null) {
            return LimitHandler.empty();
        }
        int offset = IntUtil.toInt(limitParameter.limit) <= 0 ? -1 : limitParameter.limit;
        return LimitHandler.limit(limitParameter.start, offset);
    }

    protected Collection<String> mergeIncludeColumns(Set<String> includeColumns, Set<String> excludeColumns) {

        if (!includeColumns.isEmpty()) {
            return includeColumns;
        }

        if (excludeColumns.isEmpty()) {
            return Collections.emptyList();
        }

        // 过滤掉 exclude
        List<EntityTableField> tableFieldList = context.getEntityTable().getEntityTableFieldList();
        return tableFieldList.stream()
                .map(tableField -> tableField.getTableFieldMetadata().getField())
                .filter(columnName -> !excludeColumns.contains(columnName))
                .collect(Collectors.toList());
    }
}
