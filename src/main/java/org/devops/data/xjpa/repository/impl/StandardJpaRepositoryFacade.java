package org.devops.data.xjpa.repository.impl;

import org.devops.data.xjpa.annotation.SkipRepositoryScan;
import org.devops.data.xjpa.join.JoinModel;
import org.devops.data.xjpa.lifecycle.Closeable;
import org.devops.data.xjpa.lifecycle.Disposable;
import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.devops.data.xjpa.repository.UpdateOperator;
import org.devops.data.xjpa.repository.UpdateRequest;
import org.devops.data.xjpa.sql.executor.SortType;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;

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
    @ReturnThis
    public StandardJpaRepository<K, V> descByColumn(String column) {
        return local().descByColumn(column);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> ascByColumn(String column) {
        return local().ascByColumn(column);
    }

    @Override
    @DisposeAfterReturn
    public boolean isExists() {
        return local().isExists();
    }

    @Override
    @DisposeAfterReturn
    public boolean isExistsById(K key) {
        return local().isExistsById(key);
    }

    @Override
    @DisposeAfterReturn
    public long count() {
        return local().count();
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> include(String... columns) {
        return local().include(columns);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> exclude(String... columns) {
        return local().exclude(columns);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> limit(int size) {
        return local().limit(size);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> limit(int start, int size) {
        return local().limit(start, size);
    }

    @Override
    public StandardJpaRepository<K, V> limit(boolean isPage, int start, int size) {
        return isPage ? limit(start, size) : this;
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> distinct(String... columns) {
        return local().distinct(columns);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> ignoreSoftDelete() {
        return local().ignoreSoftDelete();
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> groupByColumns(String... columns) {
        return local().groupByColumns(columns);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> having(String havingString) {
        return local().having(havingString);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> orderString(String orderByString) {
        return local().orderString(orderByString);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> orderByColumn(String column, SortType sortType) {
        return local().orderByColumn(column, sortType);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(boolean valid, IQueryWhereObject whereValue) {
        return local().where(valid, whereValue);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(IQueryWhereObject whereValue) {
        return local().where(whereValue);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(String column, Object value) {
        return local().where(column, value);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(boolean valid, String column, Object value) {
        return local().where(valid, column, value);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(String column, WhereOperator operator) {
        return local().where(column, operator);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(boolean valid, String column, WhereOperator operator) {
        return local().where(valid, column, operator);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(String column, Object value, WhereOperator operator) {
        return local().where(column, value, operator);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(boolean valid, String column, Object value, WhereOperator operator) {
        return local().where(valid, column, value, operator);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(String column, WhereOperator operator, Condition condition) {
        return local().where(column, operator, condition);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(boolean valid, String column, WhereOperator operator, Condition condition) {
        return local().where(valid, column, operator, condition);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(String column, Object value, Condition condition) {
        return local().where(column, value, condition);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(boolean valid, String column, Object value, Condition condition) {
        return local().where(valid, column, value, condition);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(String column, Object value, WhereOperator operator, Condition condition) {
        return local().where(column, value, operator, condition);
    }

    @Override
    @ReturnThis
    public StandardJpaRepository<K, V> where(boolean valid, String column, Object value, WhereOperator operator, Condition condition) {
        return local().where(valid, column, operator, condition);
    }

    @Override
    public void clear() {
        local().clear();
    }

    @Override
    @DisposeAfterReturn
    public int deleteById(K key) {
        return local().deleteById(key);
    }

    @Override
    @DisposeAfterReturn
    public int deleteByIds(Collection<K> keys) {
        return local().deleteByIds(keys);
    }

    @Override
    @DisposeAfterReturn
    public int delete(Collection<V> entities) {
        return local().delete(entities);
    }

    @Override
    @DisposeAfterReturn
    public int delete() {
        return local().delete();
    }

    @Override
    @DisposeAfterReturn
    public int insert(V entities) {
        return local().insert(entities);
    }

    @Override
    @DisposeAfterReturn
    public int insert(Collection<V> entity) {
        return local().insert(entity);
    }

    @Override
    @DisposeAfterReturn
    public List<V> list() {
        return local().list();
    }

    @Override
    @DisposeAfterReturn
    public List<V> listByIds(Collection<K> keys) {
        return local().listByIds(keys);
    }

    @Override
    @DisposeAfterReturn
    public V get() {
        return local().get();
    }

    @Override
    @DisposeAfterReturn
    public V getById(K key) {
        return local().getById(key);
    }

    @Override
    @DisposeAfterReturn
    public <T> T get(Class<T> resultType) {
        return local().get(resultType);
    }

    @Override
    @DisposeAfterReturn
    public <T> List<T> list(Class<T> resultType) {
        return local().list(resultType);
    }

    @Override
    @DisposeAfterReturn
    public <T> List<T> listSingleColumn(Class<T> clazz) {
        return local().listSingleColumn(clazz);
    }

    @Override
    @DisposeAfterReturn
    public <T> T getSingleColumn(Class<T> clazz) {
        return local().getSingleColumn(clazz);
    }

    @Override
    @DisposeAfterReturn
    public int update(String targetColumn, String operateColumn, Object operateValue, UpdateOperator updateOperator) {
        return local().update(targetColumn,operateColumn, operateValue, updateOperator);
    }

    @Override
    @DisposeAfterReturn
    public int update(UpdateRequest updateRequest) {
        return local().update(updateRequest);
    }

    @Override
    @DisposeAfterReturn
    public int update(V entity) {
        return local().update(entity);
    }

    @Override
    @DisposeAfterReturn
    public int update(Collection<V> entities) {
        return local().update(entities);
    }

    @Override
    @DisposeAfterReturn
    public int add(String targetColumn, Object operateValue) {
        return local().add(targetColumn, operateValue);
    }

    @Override
    @DisposeAfterReturn
    public int add(String targetColumn, String operateColumn, Object operateValue) {
        return local().add(targetColumn, operateColumn, operateValue);
    }

    @Override
    @DisposeAfterReturn
    public int subtract(String targetColumn, Object operateValue) {
        return local().subtract(targetColumn, operateValue);
    }

    @Override
    @DisposeAfterReturn
    public int subtract(String targetColumn, String operateColumn, Object operateValue) {
        return local().subtract(targetColumn, operateColumn, operateValue);
    }

    @Override
    @DisposeAfterReturn
    public int multiply(String targetColumn, Object operateValue) {
        return local().multiply(targetColumn, operateValue);
    }

    @Override
    @DisposeAfterReturn
    public int multiply(String targetColumn, String operateColumn, Object operateValue) {
        return local().multiply(targetColumn, operateColumn, operateValue);
    }

    @Override
    @DisposeAfterReturn
    public int divide(String targetColumn, Object operateValue) {
        return local().divide(targetColumn, operateValue);
    }

    @Override
    @DisposeAfterReturn
    public int divide(String targetColumn, String operateColumn, Object operateValue) {
        return local().divide(targetColumn, operateColumn, operateValue);
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
