package com.glee.xjpa.io;

import com.glee.xjpa.io.update.UpdateSets;
import com.glee.xjpa.sql.where.usermodel.QueryWhere;

import java.io.Serializable;
import java.util.*;

/**
 * @author GENSEN
 * @param <K> 主键类型
 * @param <E> 实体类类型
 */
public interface StandardXJpaRepository<K extends Serializable, E> {

    int update(QueryWhere where, UpdateSets updateSets);

    int update(Set<E> entities);

    default int update(List<E> entities) {
        return update(new HashSet<>(entities));
    }

    boolean update(E entity);

    boolean insert(E entity);

    int insert(List<E> entities);

    boolean delete(K id);

    int delete(Set<K> ids);

    int delete(QueryWhere where);

    List<E> listByIds(Set<K> ids);

    List<E> listAll();

    List<E> list(Query query);

    List<Map<String, Object>> list(GroupByInQuery query);

    long count(QueryWhere where);

    long count(QueryWhere where, String countColumn);

    E getById(K id);

    E get(Query query);

    Optional<E> findById(K id);

    Optional<E> find(Query query);

}
