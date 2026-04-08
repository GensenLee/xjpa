package com.glee.xjpa.proxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.exception.XJpaException;
import com.glee.xjpa.io.*;
import com.glee.xjpa.io.include.IncludeCountingColumn;
import com.glee.xjpa.io.update.UpdateRequest;
import com.glee.xjpa.io.update.UpdateSets;
import com.glee.xjpa.sql.executor.InsertSqlExecutor;
import com.glee.xjpa.sql.executor.TypeReadSqlExecutor;
import com.glee.xjpa.sql.executor.UpdateSqlExecutor;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.sql.result.JsonResultConverter;
import com.glee.xjpa.sql.result.MapListResultConverter;
import com.glee.xjpa.sql.where.usermodel.QueryWhere;
import com.glee.xjpa.table.TableProperties;
import com.glee.xjpa.table.XJpaTableMetadata;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author GENSEN
 * @date 2026/1/20
 * @description repository代理实现
 */
public class XJpaStandardRepositoryImpl<K extends Serializable, E> implements StandardXJpaRepository<K, E> {

    private final TableProperties<K, E> tableProperties;
    private final TypeReadSqlExecutor<K, E> typeReadSqlExecutor;
    private final UpdateSqlExecutor<K, E> updateSqlExecutor;
    private final InsertSqlExecutor<K, E> insertSqlExecutor;

    public XJpaStandardRepositoryImpl(TableProperties<K, E> tableProperties, DataSourceManager dataSourceManager,
                                      SqlLogger sqlLogger) {
        this.tableProperties = tableProperties;
        this.typeReadSqlExecutor = new TypeReadSqlExecutor<>(tableProperties, dataSourceManager, sqlLogger);
        this.updateSqlExecutor = new UpdateSqlExecutor<>(tableProperties, dataSourceManager, sqlLogger);
        this.insertSqlExecutor = new InsertSqlExecutor<>(tableProperties, dataSourceManager, sqlLogger);
    }


    @Override
    public int update(QueryWhere where, UpdateSets updateSets) {
        UpdateRequest request = new UpdateRequest(tableProperties, updateSets, where);
        return updateSqlExecutor.execute(request);
    }

    @Override
    public int update(Set<E> entities) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new XJpaException("Batch update must be executed within a transaction");
        }
        return updateSqlExecutor.execute(entities);
    }

    @Override
    public boolean update(E entity) {
        return updateSqlExecutor.execute(entity) > 0;
    }

    @Override
    public boolean insert(E entity) {
        return insert(List.of(entity)) > 0;
    }

    @Override
    public int insert(List<E> entities) {
        InsertQueryRequest insertQueryRequest = new InsertQueryRequest(entities, tableProperties);
        return insertSqlExecutor.execute(insertQueryRequest);
    }

    @Override
    public boolean delete(K id) {
        String keyColumnName = tableProperties.getMetadata().getPrimaryKeyColumnName();
        QueryWhere where = new QueryWhere();
        where.andEqual(keyColumnName, id);
        return delete(where) > 0;
    }

    @Override
    public int delete(Set<K> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return 0;
        }
        String keyColumnName = tableProperties.getMetadata().getPrimaryKeyColumnName();
        QueryWhere where = new QueryWhere();
        where.andIn(keyColumnName, ids);
        return delete(where);
    }

    @Override
    public int delete(QueryWhere where) {
        Query query = new Query();
        query.where(where);
        DeleteQueryRequest deleteQueryRequest = new DeleteQueryRequest(query, tableProperties);
        return updateSqlExecutor.execute(deleteQueryRequest);
    }

    @Override
    public List<E> listByIds(Set<K> ids) {
        XJpaTableMetadata<K, E> metadata = tableProperties.getMetadata();
        String keyColumnName = metadata.getPrimaryKeyColumnName();
        Query query = new Query();
        QueryWhere where = new QueryWhere();
        where.andIn(keyColumnName, ids);
        query.where(where);
        query.limit(0, ids.size());
        ReadQueryRequest request = new ReadQueryRequest(query, tableProperties);
        return doQueryAndConvert(request);
    }

    @Override
    public List<E> listAll() {
        ReadQueryRequest request = new ReadQueryRequest(new Query(), tableProperties);
        return doQueryAndConvert(request);
    }

    @Override
    public List<E> list(Query query) {
        ReadQueryRequest request = new ReadQueryRequest(query, tableProperties);
        return doQueryAndConvert(request);
    }

    @Override
    public List<Map<String, Object>> list(GroupByInQuery query) {
        GroupingReadQueryRequest request = new GroupingReadQueryRequest(query, tableProperties);
        return typeReadSqlExecutor.execute(request, new MapListResultConverter());
    }

    private List<E> doQueryAndConvert(QueryRequest request) {
        JsonResultConverter<E> converter = new JsonResultConverter<>(tableProperties.getMetadata().getEntityType(), new MapListResultConverter());
        return typeReadSqlExecutor.execute(request, converter);
    }

    @Override
    public long count(QueryWhere where) {
        XJpaTableMetadata<K, E> metadata = tableProperties.getMetadata();
        String name = metadata.getPrimaryKeyColumnName();
        return count(where, name);
    }

    @Override
    public long count(QueryWhere where, String countColumn) {
        Query query = new Query();
        query.where(where);
        query.include(new IncludeCountingColumn(countColumn, "__count__"));
        ReadQueryRequest request = new ReadQueryRequest(query, tableProperties);
        List<Map<String, Object>> mapList = typeReadSqlExecutor.execute(request, new MapListResultConverter());

        Map<String, Object> first = CollectionUtil.getFirst(mapList);
        if (first == null) {
            return 0;
        }
        return MapUtil.get(first, "__count__", Long.class, 0L);
    }

    @Override
    public E getById(K id) {
        XJpaTableMetadata<K, E> metadata = tableProperties.getMetadata();
        String keyColumnName = metadata.getPrimaryKeyColumnName();
        Query query = new Query();
        QueryWhere where = new QueryWhere();
        where.andEqual(keyColumnName, id);
        query.where(where);
        query.limit(0, 1);
        return get(query);
    }

    @Override
    public E get(Query query) {
        ReadQueryRequest request = new ReadQueryRequest(query, tableProperties);
        List<E> result = doQueryAndConvert(request);
        return CollectionUtil.getFirst(result);
    }

    @Override
    public Optional<E> findById(K id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<E> find(Query query) {
        return Optional.ofNullable(get(query));
    }
}
