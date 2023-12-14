package org.devops.data.xjpa.repository.impl.enhance;

import cn.hutool.core.util.StrUtil;
import org.devops.data.xjpa.constant.XjpaConstant;
import org.devops.data.xjpa.repository.IEnhanceRepository;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.executor.LimitHandler;
import org.devops.data.xjpa.sql.executor.SortHandler;
import org.devops.data.xjpa.sql.executor.SortType;
import org.springframework.util.Assert;

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
        Assert.notEmpty(columns, "distinct columns must be set");
        this.groupByColumns.addAll(Arrays.stream(columns).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> having(String havingString) {
        Assert.hasLength(havingString, "havingString must be set");
        this.havingString = havingString;
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> distinct(String... columns) {
        Assert.notEmpty(columns, "distinct columns must be set");
        this.distinct.addAll(Arrays.stream(columns).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> include(String... columns) {
        Assert.notEmpty(columns, "distinct columns must be set");
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
        Assert.hasLength(orderByString, "empty orderString");
        Assert.isTrue(orderTypes.isEmpty(), "can not use #orderString and #orderByColumn at the same time");
        this.orderString = orderByString;
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> orderByColumn(String column, SortType sortType) {
        Assert.isTrue(StrUtil.isEmpty(orderString), "can not use #orderString and #orderByColumn at the same time");
        Assert.hasLength(column, "sort column required");
        Assert.notNull(sortType, "sort type required");

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
        return StrUtil.emptyToDefault(havingString, XjpaConstant.EMPTY_STRING);
    }

    @Override
    public boolean isIgnoreSoftDelete() {
        return false;
    }


}
