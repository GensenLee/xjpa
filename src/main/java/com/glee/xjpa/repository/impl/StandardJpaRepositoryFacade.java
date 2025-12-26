package com.glee.xjpa.repository.impl;

import com.glee.xjpa.annotation.SkipRepositoryScan;
import com.glee.xjpa.join.JoinModel;
import com.glee.xjpa.lifecycle.Closeable;
import com.glee.xjpa.lifecycle.Disposable;
import com.glee.xjpa.repository.StandardJpaRepository;
import com.glee.xjpa.repository.UpdateOperator;
import com.glee.xjpa.repository.UpdateRequest;
import com.glee.xjpa.sql.executor.SortType;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import com.glee.xjpa.sql.where.operate.Condition;
import com.glee.xjpa.sql.where.operate.WhereOperator;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/8
 * @description 外观
 */
@SkipRepositoryScan
public class StandardJpaRepositoryFacade<K extends Serializable, V> implements Facade, StandardJpaRepository<K, V>, 
        Disposable, Closeable {

    final Class<K> keyType;

    final Class<V> entityType;

    final RepositoryDelegateHolder<K, V> delegate;


    public StandardJpaRepositoryFacade(Class<K> keyType, Class<V> entityType, RepositoryDelegateHolder<K, V> delegate) {
        this.keyType = keyType;
        this.entityType = entityType;
        this.delegate = delegate;
    }

    /**
     * @return
     */
    private StandardJpaRepository<K, V> local(){
        return delegate.getDelegate(this);
    }


    @Override
    public StandardJpaRepository<K, V> descByColumn(String column) {
        local().descByColumn(column);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> ascByColumn(String column) {
        local().ascByColumn(column);
        return this;
    }

    @Override
    public boolean isExists() {
        return local().isExists();
    }

    @Override
    public boolean isExistsById(K key) {
        return local().isExistsById(key);
    }

    @Override
    public long count() {
        return local().count();
    }

    @Override
    public StandardJpaRepository<K, V> include(String... columns) {
        local().include(columns);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> exclude(String... columns) {
        local().exclude(columns);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> limit(int size) {
        local().limit(size);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> limit(int start, int size) {
        local().limit(start, size);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> limit(boolean isPage, int start, int size) {
        if (isPage) {
            limit(start, size);
        }
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> distinct(String... columns) {
        local().distinct(columns);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> ignoreSoftDelete() {
        local().ignoreSoftDelete();
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> groupByColumns(String... columns) {
        local().groupByColumns(columns);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> having(String havingString) {
        local().having(havingString);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> orderString(String orderByString) {
        local().orderString(orderByString);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> orderByColumn(String column, SortType sortType) {
        local().orderByColumn(column, sortType);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(boolean valid, IQueryWhereObject whereValue) {
        local().where(valid, whereValue);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(IQueryWhereObject whereValue) {
        local().where(whereValue);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(String column, Object value) {
        local().where(column, value);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(boolean valid, String column, Object value) {
        local().where(valid, column, value);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(String column, WhereOperator operator) {
        local().where(column, operator);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(boolean valid, String column, WhereOperator operator) {
        local().where(valid, column, operator);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(String column, Object value, WhereOperator operator) {
        local().where(column, value, operator);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(boolean valid, String column, Object value, WhereOperator operator) {
        local().where(valid, column, value, operator);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(String column, WhereOperator operator, Condition condition) {
        local().where(column, operator, condition);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(boolean valid, String column, WhereOperator operator, Condition condition) {
        local().where(valid, column, operator, condition);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(String column, Object value, Condition condition) {
        local().where(column, value, condition);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(boolean valid, String column, Object value, Condition condition) {
        local().where(valid, column, value, condition);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(String column, Object value, WhereOperator operator, Condition condition) {
        local().where(column, value, operator, condition);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> where(boolean valid, String column, Object value, WhereOperator operator, Condition condition) {
        local().where(valid, column, operator, condition);
        return this;
    }

    @Override
    public void clear() {
        local().clear();
    }

    @Override
    public int deleteById(K key) {
        return local().deleteById(key);
    }

    @Override
    public int deleteByIds(Collection<K> keys) {
        return local().deleteByIds(keys);
    }

    @Override
    public int delete(Collection<V> entities) {
        return local().delete(entities);
    }

    @Override
    public int delete() {
        return local().delete();
    }

    @Override
    public int insert(V entities) {
        return local().insert(entities);
    }

    @Override
    public int insert(Collection<V> entity) {
        return local().insert(entity);
    }

    @Override
    public List<V> list() {
        return local().list();
    }

    @Override
    public List<V> listByIds(Collection<K> keys) {
        return local().listByIds(keys);
    }

    @Override
    public V get() {
        return local().get();
    }

    @Override
    public V getById(K key) {
        return local().getById(key);
    }

    @Override
    public <T> T get(Class<T> resultType) {
        return local().get(resultType);
    }

    @Override
    public <T> List<T> list(Class<T> resultType) {
        return local().list(resultType);
    }

    @Override
    public <T> List<T> listSingleColumn(Class<T> clazz) {
        return local().listSingleColumn(clazz);
    }

    @Override
    public <T> T getSingleColumn(Class<T> clazz) {
        return local().getSingleColumn(clazz);
    }

    @Override
    public int update(String column, String operateColumn, Object operateValue, UpdateOperator updateOperator) {
        return local().update(column,operateColumn, operateValue, updateOperator);
    }

    @Override
    public int update(UpdateRequest updateRequest) {
        return local().update(updateRequest);
    }

    @Override
    public int update(V entity) {
        return local().update(entity);
    }

    @Override
    public int update(Collection<V> entities) {
        return local().update(entities);
    }

    @Override
    public int add(String column, Object operateValue) {
        return local().add(column, operateValue);
    }

    @Override
    public int add(String column, String operateColumn, Object operateValue) {
        return local().add(column, operateColumn, operateValue);
    }

    @Override
    public int subtract(String column, Object operateValue) {
        return local().subtract(column, operateValue);
    }

    @Override
    public int subtract(String column, String operateColumn, Object operateValue) {
        return local().subtract(column, operateColumn, operateValue);
    }

    @Override
    public int multiply(String column, Object operateValue) {
        return local().multiply(column, operateValue);
    }

    @Override
    public int multiply(String column, String operateColumn, Object operateValue) {
        return local().multiply(column, operateColumn, operateValue);
    }

    @Override
    public int divide(String column, Object operateValue) {
        return local().divide(column, operateValue);
    }

    @Override
    public int divide(String column, String operateColumn, Object operateValue) {
        return local().divide(column, operateColumn, operateValue);
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    @Override
    public Object getActual() {
        return local();
    }

    @Override
    public JoinModel leftJoin(Class<?> rightEntityType) {
        return local().leftJoin(rightEntityType);
    }

    @Override
    public JoinModel rightJoin(Class<?> rightEntityType) {
        return local().rightJoin(rightEntityType);
    }

    @Override
    public JoinModel innerJoin(Class<?> rightEntityType) {
        return local().innerJoin(rightEntityType);
    }
}
