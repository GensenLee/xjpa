package org.devops.data.xjpa.repository.impl.enhance;

import cn.hutool.core.util.StrUtil;
import org.devops.data.xjpa.constant.XjpaConstant;
import org.devops.data.xjpa.repository.IEnhanceRepository;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.repository.impl.RepositoryContextObserver;
import org.devops.data.xjpa.sql.executor.LimitHandler;
import org.devops.data.xjpa.sql.executor.SortHandler;
import org.devops.data.xjpa.sql.executor.SortType;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/9
 * @description 基于ThreadLocal的EnhanceCurdBound
 */
public class ThreadLocalEnhanceCurdBound<K extends Serializable, V> extends AbstractEnhanceCurdBound<K, V>
        implements RepositoryContextObserver {

    private final ThreadLocal<Set<String>> includeColumnsThreadLocal = ThreadLocal.withInitial(HashSet::new);
    private final ThreadLocal<Set<String>> systemIncludeColumnsThreadLocal = ThreadLocal.withInitial(HashSet::new);
    private final ThreadLocal<Set<String>> groupByColumnsThreadLocal = ThreadLocal.withInitial(HashSet::new);
    private final ThreadLocal<Set<String>> excludeColumnsThreadLocal = ThreadLocal.withInitial(HashSet::new);
    private final ThreadLocal<List<OrderParameter>> orderTypesThreadLocal = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<String> orderStringThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<LimitParameter> limitParameterThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<String[]> distinctThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Boolean> ignoreSoftDeleteLocal = new ThreadLocal<>();

    private final ThreadLocal<String> havingStringThreadLocal = new ThreadLocal<>();


    public ThreadLocalEnhanceCurdBound(RepositoryContext<K, V> context) {
        super(context);
        context.register(this);
    }

    @Override
    public IEnhanceRepository<K, V> ignoreSoftDelete() {
        ignoreSoftDeleteLocal.set(true);
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> groupByColumns(String... columns) {
        Assert.notEmpty(columns, "distinct columns must be set");

        groupByColumnsThreadLocal.set(Arrays.stream(columns).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> having(String havingString) {
        Assert.hasLength(havingString, "havingString must be set");

        havingStringThreadLocal.set(havingString);
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> distinct(String... columns) {
        Assert.notEmpty(columns, "distinct columns must be set");
        distinctThreadLocal.set(columns);
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> include(String... columns) {
        Assert.notEmpty(columns, "distinct columns must be set");

        Set<String> includeColumns = includeColumnsThreadLocal.get();
        includeColumns.addAll(Arrays.stream(columns).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> exclude(String... columns) {
        if (columns.length <= 0) {
            return this;
        }
        Set<String> excludeColumns = excludeColumnsThreadLocal.get();
        excludeColumns.addAll(Arrays.stream(columns).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> limit(int size) {
        LimitParameter parameter = new LimitParameter(0, size);
        limitParameterThreadLocal.set(parameter);
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> limit(int start, int size) {
        LimitParameter parameter = new LimitParameter(start, size);
        limitParameterThreadLocal.set(parameter);
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> limit(boolean isPage, int start, int size) {
        return isPage ? limit(start, size) : this;
    }

    @Override
    public IEnhanceRepository<K, V> orderString(String orderByString) {
        Assert.hasLength(orderByString, "empty orderString");
        Assert.isTrue(orderTypesThreadLocal.get().isEmpty(), "can not use #orderString and #orderByColumn at the same time");

        orderStringThreadLocal.set(orderByString);
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> orderByColumn(String column, SortType sortType) {
        Assert.isTrue(StrUtil.isEmpty(orderStringThreadLocal.get()), "can not use #orderString and #orderByColumn at the same time");
        Assert.hasLength(column, "sort column required");
        Assert.notNull(sortType, "sort type required");

        List<OrderParameter> orderParameterList = orderTypesThreadLocal.get();
        orderParameterList.add(new OrderParameter(column, sortType));
        return this;
    }

    @Override
    public LimitHandler getLimitHandler() {
        LimitParameter limitParameter = limitParameterThreadLocal.get();
        return createLimitHandler(limitParameter);
    }

    @Override
    public SortHandler getSortHandler() {
        return createSortHandler(orderStringThreadLocal.get(), orderTypesThreadLocal.get());
    }

    @Override
    public Collection<String> getIncludeColumns() {
        Collection<String> includeColumns = mergeIncludeColumns(includeColumnsThreadLocal.get(), excludeColumnsThreadLocal.get());

        if (includeColumns.isEmpty()) {
            return includeColumns;
        }

        if (!systemIncludeColumnsThreadLocal.get().isEmpty()) {
            includeColumns.addAll(systemIncludeColumnsThreadLocal.get());
        }

        return includeColumns;
    }

    @Override
    public Collection<String> getDistinctColumns() {
        String[] distinct = distinctThreadLocal.get();

        if (distinct == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(distinct).collect(Collectors.toList());
    }

    @Override
    public Collection<String> getGroupingColumns() {
        return groupByColumnsThreadLocal.get();
    }

    @Override
    public String getHavingString() {
        return StrUtil.emptyToDefault(havingStringThreadLocal.get(), XjpaConstant.EMPTY_STRING);
    }

    @Override
    public boolean isIgnoreSoftDelete() {
        Boolean value = ignoreSoftDeleteLocal.get();
        return Boolean.TRUE.equals(value);
    }


    /**
     * @param columns
     */
    public void setSystemIncludes(String... columns) {
        if (columns.length == 0) {
            systemIncludeColumnsThreadLocal.remove();
            return;
        }
        systemIncludeColumnsThreadLocal.set(new HashSet<>(Arrays.asList(columns)));
    }


    @SuppressWarnings({"rawtypes"})
    @Override
    public void onDispose(RepositoryContext context) {
        includeColumnsThreadLocal.remove();
        excludeColumnsThreadLocal.remove();
        orderTypesThreadLocal.remove();
        orderStringThreadLocal.remove();
        limitParameterThreadLocal.remove();
        groupByColumnsThreadLocal.remove();
        systemIncludeColumnsThreadLocal.remove();
        ignoreSoftDeleteLocal.remove();
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public void onClose(RepositoryContext context) {
        includeColumnsThreadLocal.remove();
        excludeColumnsThreadLocal.remove();
        orderTypesThreadLocal.remove();
        orderStringThreadLocal.remove();
        limitParameterThreadLocal.remove();
        groupByColumnsThreadLocal.remove();
        systemIncludeColumnsThreadLocal.remove();
        ignoreSoftDeleteLocal.remove();
    }


    @Override
    public String toString() {
        return "ThreadLocalEnhanceCurdBound{" +
                "includeColumnsThreadLocal=" + includeColumnsThreadLocal.get() +
                ", systemIncludeColumnsThreadLocal=" + systemIncludeColumnsThreadLocal.get() +
                ", groupByColumnsThreadLocal=" + groupByColumnsThreadLocal.get() +
                ", excludeColumnsThreadLocal=" + excludeColumnsThreadLocal.get() +
                ", orderTypesThreadLocal=" + orderTypesThreadLocal.get() +
                ", orderStringThreadLocal=" + orderStringThreadLocal.get() +
                ", limitParameterThreadLocal=" + limitParameterThreadLocal.get() +
                ", distinctThreadLocal=" + Arrays.toString(distinctThreadLocal.get()) +
                ", ignoreSoftDeleteLocal=" + ignoreSoftDeleteLocal.get() +
                ", havingStringThreadLocal=" + havingStringThreadLocal.get() +
                '}';
    }
}
