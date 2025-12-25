package com.glee.xjpa.repository.impl.curd;

import cn.hutool.core.util.NumberUtil;
import com.glee.xjpa.sql.where.operate.WhereOperator;
import com.glee.xjpa.constant.XjpaConstant;
import com.glee.xjpa.repository.ISelectRepository;
import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.repository.impl.RepositoryContextBean;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;
import com.glee.xjpa.sql.executor.LimitHandler;
import com.glee.xjpa.sql.executor.SelectSqlExecutor;
import com.glee.xjpa.sql.executor.SortHandler;
import com.glee.xjpa.sql.executor.command.DefaultQueryExecuteRequestCommandAcceptor;
import com.glee.xjpa.sql.executor.command.QueryExecuteRequestCommandAcceptor;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.executor.query.QueryRequestBuilder;
import com.glee.xjpa.sql.executor.query.SelectQueryRequest;
import com.glee.xjpa.sql.where.usermodel.XQueryWhereValue;
import com.glee.xjpa.table.EntityTableField;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description select代理实现
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SelectRepositoryProxyImpl<K extends Serializable, V> extends RepositoryContextBean<K, V> implements ISelectRepository<K, V> {

    private final QueryExecuteRequestCommandAcceptor queryExecuteRequestCommandAcceptor;

    private final EnhanceCurdBound enhanceCurdBound;

    protected SelectRepositoryProxyImpl(RepositoryContext<K, V> context, EnhanceCurdBound enhanceCurdBound) {
        super(context);
        this.queryExecuteRequestCommandAcceptor = new DefaultQueryExecuteRequestCommandAcceptor<>(context);
        this.enhanceCurdBound = enhanceCurdBound;
    }

    @Override
    public List<V> list() {
        SelectColumn selectColumn = createSelectColumn();

        return doGetList(getContext().getEntityTable().getEntityType(), 
                enhanceCurdBound.getLimitHandler(),
                enhanceCurdBound.getSortHandler(),
                selectColumn.includedColumnList,
                selectColumn.distinct,
                enhanceCurdBound.getGroupingColumns(),
                enhanceCurdBound.getHavingString());
    }

    @Override
    public List<V> listByIds(Collection<K> keys) {
        Assert.notEmpty(keys, "keys required");

        SelectColumn selectColumn = createSelectColumn();

        context.localQueryWhere().clear();
        EntityTableField primaryKeyField = context.getEntityTable().getPrimaryKeyField();
        Assert.notNull(primaryKeyField, "table does not has a Primary Key");
        context.localQueryWhere().add(new XQueryWhereValue(primaryKeyField.getTableFieldMetadata().getField(), keys, WhereOperator.IN));

        return doGetList(getContext().getEntityTable().getEntityType(),
                LimitHandler.limit(keys.size(), 0),
                SortHandler.empty(),
                selectColumn.includedColumnList,
                selectColumn.distinct,
                enhanceCurdBound.getGroupingColumns(),
                enhanceCurdBound.getHavingString());
    }

    @Override
    public <T> List<T> list(Class<T> resultType) {
        Assert.notNull(resultType, "result type required");

        SelectColumn selectColumn = createSelectColumn();

        return doGetList(resultType, enhanceCurdBound.getLimitHandler(),
                enhanceCurdBound.getSortHandler(),
                selectColumn.includedColumnList,
                selectColumn.distinct,
                enhanceCurdBound.getGroupingColumns(), 
                enhanceCurdBound.getHavingString());
    }

    @Override
    public V get() {
        List<V> result = doGetList(getContext().getEntityTable().getEntityType(),
                LimitHandler.limit(1, 0),
                enhanceCurdBound.getSortHandler(),
                enhanceCurdBound.getIncludeColumns(),
                false,
                enhanceCurdBound.getGroupingColumns(),
                enhanceCurdBound.getHavingString());
        return CollectionUtils.firstElement(result);
    }

    @Override
    public V getById(K key) {
        Assert.notNull(key, "key required");
        context.localQueryWhere().clear();
        EntityTableField primaryKeyField = context.getEntityTable().getPrimaryKeyField();
        Assert.notNull(primaryKeyField, "table does not has a Primary Key");
        context.localQueryWhere().add(new XQueryWhereValue(primaryKeyField.getTableFieldMetadata().getField(), key));

        List<V> result = listByIds(Collections.singleton(key));
        return CollectionUtils.firstElement(result);
    }

    @Override
    public <T> T get(Class<T> resultType) {
        Assert.notNull(resultType, "result type required");
        
        List<T> result = doGetList(resultType, LimitHandler.limit(1, 0),
                enhanceCurdBound.getSortHandler(),
                enhanceCurdBound.getIncludeColumns(), 
                false, 
                enhanceCurdBound.getGroupingColumns(), 
                enhanceCurdBound.getHavingString());

        return CollectionUtils.firstElement(result);
    }

    @Override
    public boolean isExists() {
        EntityTableField primaryKeyField = getContext().getEntityTable().pickTableField();

        SelectColumn selectColumn = createSelectColumn();

        Collection<String> includedColumns = CollectionUtils.isEmpty(selectColumn.includedColumnList) ?
                Collections.singletonList(primaryKeyField.getTableFieldMetadata().getField()) : selectColumn.includedColumnList;

        List<V> result = doGetList(getContext().getEntityTable().getEntityType(),
                LimitHandler.limit(1, 0), SortHandler.empty(),
                includedColumns,
                selectColumn.distinct,
                Collections.emptyList(),
                XjpaConstant.EMPTY_STRING);
        return !CollectionUtils.isEmpty(result);
    }

    @Override
    public boolean isExistsById(K key) {
        context.localQueryWhere().clear();
        EntityTableField primaryKeyField = context.getEntityTable().getPrimaryKeyField();
        Assert.notNull(primaryKeyField, "table does not has a Primary Key");
        context.localQueryWhere().add(new XQueryWhereValue(primaryKeyField.getTableFieldMetadata().getField(), key));

        List<V> result = doGetList(getContext().getEntityTable().getEntityType(),
                LimitHandler.limit(1, 0), SortHandler.empty(),
                Collections.singletonList(primaryKeyField.getTableFieldMetadata().getField()),
                false, Collections.emptyList(), XjpaConstant.EMPTY_STRING);
        return !CollectionUtils.isEmpty(result);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public long count() {
        EntityTableField primaryKeyField = getContext().getEntityTable().pickTableField();

        SelectColumn selectColumn = createSelectColumn();

        String countColumn = CollectionUtils.isEmpty(selectColumn.includedColumnList) ?
                primaryKeyField.getTableFieldMetadata().getField() :
                selectColumn.includedColumnList.iterator().next();

        List<Map> result = doGetList(Map.class,
                LimitHandler.limit(1, 0),
                SortHandler.empty(),
                Collections.singletonList("count(" + countColumn + ") as __count__"),
                selectColumn.distinct,
                Collections.emptyList(),
                XjpaConstant.EMPTY_STRING);

        Map countResult = CollectionUtils.firstElement(result);
        return CollectionUtils.isEmpty(countResult) ? 0 :
                NumberUtil.parseLong(String.valueOf(countResult.getOrDefault("__count__", 0)));
    }

    /**
     * @param clazz
     * @param limitHandler
     * @param sortHandler
     * @param includeColumns
     * @param distinct
     * @param <T>
     * @return
     */
    private <T> List<T> doGetList(Class<T> clazz, LimitHandler limitHandler, SortHandler sortHandler,
                                  Collection<String> includeColumns, boolean distinct, 
                                  Collection<String> groupingColumns, String havingString) {
        AbstractQueryRequest<K, V> selectQueryRequest = QueryRequestBuilder
                .bind(SelectQueryRequest.class, context)
                .create(limitHandler, sortHandler, includeColumns, distinct, groupingColumns, havingString);

        selectQueryRequest.setQueryWhereHandler(getWhereHandler());
        return queryExecuteRequestCommandAcceptor.executeAndConvert(selectQueryRequest, SelectSqlExecutor.class, clazz);
    }


    private SelectColumn createSelectColumn(){
        Collection<String> includeColumns = enhanceCurdBound.getIncludeColumns();

        boolean distinct = !enhanceCurdBound.getDistinctColumns().isEmpty();
        if (distinct) {
            includeColumns = enhanceCurdBound.getDistinctColumns();
        }

        return new SelectColumn(distinct, includeColumns);
    }

    private static class SelectColumn {
        public SelectColumn(boolean distinct, Collection<String> includedColumnList) {
            this.distinct = distinct;
            this.includedColumnList = includedColumnList;
        }

        private final boolean distinct;
        private final Collection<String> includedColumnList;
    }

}
