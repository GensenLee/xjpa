package com.glee.xjpa.repository.impl.enhance;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.sql.executor.DefaultSortHandler;
import com.glee.xjpa.sql.executor.LimitHandler;
import com.glee.xjpa.sql.executor.SortHandler;
import com.glee.xjpa.sql.executor.SortType;
import com.glee.xjpa.table.EntityTableField;

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
        if (StrUtil.isNotEmpty(sortString)) {
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
        int offset = NumberUtil.parseInt(String.valueOf(limitParameter.limit)) <= 0 ? -1 : limitParameter.limit;
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
