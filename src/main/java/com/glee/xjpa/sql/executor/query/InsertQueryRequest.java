package com.glee.xjpa.sql.executor.query;

import com.glee.xjpa.repository.impl.RepositoryContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 插入
 */
public class InsertQueryRequest<K, V> extends AbstractQueryRequest<K, V> {

    final Collection<V> entityValues;

    public InsertQueryRequest(RepositoryContext<K, V> context, Collection<V> entityValues) {
        super(context);
        this.entityValues = entityValues;
    }

    public List<V> getEntityValues() {
        return new ArrayList<>(entityValues);
    }
}
