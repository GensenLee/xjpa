package org.devops.data.xjpa.sql.executor.query;

import lombok.Getter;
import org.devops.data.xjpa.repository.impl.RepositoryContext;

import java.util.List;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 插入
 */
@Getter
public class InsertQueryRequest<K, V> extends AbstractQueryRequest<K, V> {

    final List<V> entityValues;

    public InsertQueryRequest(RepositoryContext<K, V> context, List<V> entityValues) {
        super(context);
        this.entityValues = entityValues;
    }

}
