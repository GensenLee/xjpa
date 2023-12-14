package org.devops.data.xjpa.repository.impl.enhance;

import org.devops.core.utils.util.AssertUtil;
import org.devops.core.utils.util.StringUtil;
import org.devops.data.xjpa.repository.IEnhanceRepository;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.executor.LimitHandler;
import org.devops.data.xjpa.sql.executor.SortHandler;
import org.devops.data.xjpa.sql.executor.SortType;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2023-06-21
 * @description 一次性使用的EnhanceCurdBound
 */
public class DisposableEnhanceCurdBound<K extends Serializable, V> extends AbstractEnhanceCurdBound<K, V> {

    private final Set<String> includeColumns = new HashSet<>();
    private final Set<String> groupByColumns = new HashSet<>();
    private final Set<String> distinct = new HashSet<>();
    private final List<OrderParameter> orderTypes = new ArrayList<>();
    private String orderString;
    private LimitParameter limitParameter;
    private String havingString;


    public DisposableEnhanceCurdBound(RepositoryContext<K, V> context) {
        super(context);
    }

    @Override
    public IEnhanceRepository<K, V> ignoreSoftDelete() {
        throw new UnsupportedOperationException("ignoreSoftDelete");
    }

    @Override
    public IEnhanceRepository<K, V> groupByColumns(String... columns) {
        AssertUtil.notEmpty(columns, "distinct columns must be set");
        this.groupByColumns.addAll(Arrays.stream(columns).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> having(String havingString) {
        AssertUtil.hasLength(havingString, "havingString must be set");
        this.havingString = havingString;
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> distinct(String... columns) {
        AssertUtil.notEmpty(columns, "distinct columns must be set");
        this.distinct.addAll(Arrays.stream(columns).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> include(String... columns) {
        AssertUtil.notEmpty(columns, "distinct columns must be set");
        this.includeColumns.addAll(Arrays.stream(columns).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> exclude(String... columns) {
        throw new UnsupportedOperationException("exclude");
    }

    @Override
    public IEnhanceRepository<K, V> limit(int size) {
        this.limitParameter = new LimitParameter(0, size);
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> limit(int start, int size) {
        this.limitParameter = new LimitParameter(start, size);
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> limit(boolean isPage, int start, int size) {
        return isPage ? limit(start, size) : this;
    }

    @Override
    public IEnhanceRepository<K, V> orderString(String orderByString) {
        AssertUtil.hasLength(orderByString, "empty orderString");
        AssertUtil.isTrue(orderTypes.isEmpty(), "can not use #orderString and #orderByColumn at the same time");
        this.orderString = orderByString;
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> orderByColumn(String column, SortType sortType) {
        AssertUtil.isTrue(StringUtil.isEmpty(orderString), "can not use #orderString and #orderByColumn at the same time");
        AssertUtil.hasLength(column, "sort column required");
        AssertUtil.notNull(sortType, "sort type required");

        orderTypes.add(new OrderParameter(column, sortType));
        return this;
    }

    @Override
    public LimitHandler getLimitHandler() {
        return createLimitHandler(limitParameter);
    }

    @Override
    public SortHandler getSortHandler() {
        return createSortHandler(orderString, orderTypes);
    }

    @Override
    public Collection<String> getIncludeColumns() {
        return mergeIncludeColumns(includeColumns, Collections.emptySet());
    }

    @Override
    public Collection<String> getDistinctColumns() {
        return distinct;
    }

    @Override
    public Collection<String> getGroupingColumns() {
        return groupByColumns;
    }

    @Override
    public String getHavingString() {
        return StringUtil.getDefault(havingString, "");
    }

    @Override
    public boolean isIgnoreSoftDelete() {
        return false;
    }


}
