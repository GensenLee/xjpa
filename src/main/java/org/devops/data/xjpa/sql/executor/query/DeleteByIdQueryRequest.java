package org.devops.data.xjpa.sql.executor.query;

import lombok.Getter;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.executor.LimitHandler;

import java.util.List;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 删除
 */
@Getter
public class DeleteByIdQueryRequest<K, V> extends AbstractQueryRequest<K, V> {

    final List<K> keys;


    public DeleteByIdQueryRequest(RepositoryContext<K, V> context, List<K> keys) {
        super(context);
        this.keys = keys;
    }


    public LimitHandler getLimitHandler() {
        return LimitHandler.limit(keys.size(),  -1);
    }
}
