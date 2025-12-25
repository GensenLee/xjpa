package com.glee.xjpa.repository;


import com.glee.xjpa.annotation.SkipRepositoryScan;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import com.glee.xjpa.sql.where.operate.Condition;
import com.glee.xjpa.sql.where.operate.WhereOperator;
import com.glee.xjpa.join.JoiningTableRepository;
import com.glee.xjpa.sql.executor.SortType;

import java.io.Serializable;
import java.util.List;

@SkipRepositoryScan
public interface StandardJpaRepository<K extends Serializable, V> extends IEnhanceCurdRepository<K, V>, IRepositoryWhereAttach,
        JoiningTableRepository {

    @Override
    StandardJpaRepository<K, V> include(String... columns);

    @Override
    StandardJpaRepository<K, V> exclude(String... columns);

    @Override
    StandardJpaRepository<K, V> limit(int size);

    @Override
    StandardJpaRepository<K, V> limit(int start, int size);

    @Override
    StandardJpaRepository<K, V> limit(boolean isPage, int start, int size);

    @Override
    StandardJpaRepository<K, V> distinct(String... columns);

    @Override
    StandardJpaRepository<K, V> groupByColumns(String... columns);

    @Override
    StandardJpaRepository<K, V> having(String havingString);

    @Override
    StandardJpaRepository<K, V> orderString(String orderByString);

    @Override
    StandardJpaRepository<K, V> orderByColumn(String column, SortType sortType);

    @Override
    default StandardJpaRepository<K, V> descByColumn(String column) {
        return orderByColumn(column, SortType.desc);
    }

    @Override
    default StandardJpaRepository<K, V> ascByColumn(String column) {
        return orderByColumn(column, SortType.asc);
    }

    @Override
    StandardJpaRepository<K, V> ignoreSoftDelete();

    @Override
    StandardJpaRepository<K, V> where(boolean valid, IQueryWhereObject whereValue);

    @Override
    StandardJpaRepository<K, V> where(IQueryWhereObject whereValue);

    @Override
    StandardJpaRepository<K, V> where(String column, Object value);

    @Override
    StandardJpaRepository<K, V> where(String column, WhereOperator operator);

    @Override
    StandardJpaRepository<K, V> where(String column, Object value, WhereOperator operator);

    @Override
    StandardJpaRepository<K, V> where(String column, WhereOperator operator, Condition condition);

    @Override
    StandardJpaRepository<K, V> where(String column, Object value, Condition condition);

    @Override
    StandardJpaRepository<K, V> where(String column, Object value, WhereOperator operator, Condition condition);

    @Override
    StandardJpaRepository<K, V> where(boolean valid, String column, Object value);

    @Override
    StandardJpaRepository<K, V> where(boolean valid, String column, WhereOperator operator);

    @Override
    StandardJpaRepository<K, V> where(boolean valid, String column, WhereOperator operator, Condition condition);

    @Override
    StandardJpaRepository<K, V> where(boolean valid, String column, Object value, WhereOperator operator);

    @Override
    StandardJpaRepository<K, V> where(boolean valid, String column, Object value, Condition condition);

    @Override
    StandardJpaRepository<K, V> where(boolean valid, String column, Object value, WhereOperator operator, Condition condition);

    default List<V> list(IQueryWhereObject whereObject) {
        return where(whereObject).list();
    }

    default <T> List<T> list(IQueryWhereObject whereObject, Class<T> resultType) {
        return where(whereObject).list(resultType);
    }

    default long count(IQueryWhereObject whereObject) {
        return where(whereObject).count();
    }

    default boolean isExists(IQueryWhereObject whereObject) {
        return where(whereObject).isExists();
    }

    default V get(IQueryWhereObject whereObject) {
        return where(whereObject).get();
    }

    default int delete(IQueryWhereObject whereObject) {
        return where(whereObject).delete();
    }

}
