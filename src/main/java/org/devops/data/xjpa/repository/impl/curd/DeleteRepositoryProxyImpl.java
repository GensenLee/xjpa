package org.devops.data.xjpa.repository.impl.curd;

import org.devops.core.utils.util.AssertUtil;
import org.devops.data.xjpa.repository.IDeleteRepository;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.repository.impl.RepositoryContextBean;
import org.devops.data.xjpa.sql.executor.DeleteByIdSqlExecutor;
import org.devops.data.xjpa.sql.executor.DeleteByWhereSqlExecutor;
import org.devops.data.xjpa.sql.executor.LimitHandler;
import org.devops.data.xjpa.sql.executor.SortHandler;
import org.devops.data.xjpa.sql.executor.command.DefaultQueryExecuteRequestCommandAcceptor;
import org.devops.data.xjpa.sql.executor.command.QueryExecuteRequestCommandAcceptor;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.query.DeleteByIdQueryRequest;
import org.devops.data.xjpa.sql.executor.query.DeleteByWhereQueryRequest;
import org.devops.data.xjpa.sql.executor.query.QueryRequestBuilder;
import org.devops.data.xjpa.table.EntityTableField;
import org.devops.data.xjpa.util.EntityUtil;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description delete代理实现
 */
public class DeleteRepositoryProxyImpl<K extends Serializable, V> extends RepositoryContextBean<K, V> implements IDeleteRepository<K, V> {

    private final QueryExecuteRequestCommandAcceptor queryExecuteRequestCommandAcceptor;

    protected DeleteRepositoryProxyImpl(RepositoryContext<K, V> context) {
        super(context);
        this.queryExecuteRequestCommandAcceptor = new DefaultQueryExecuteRequestCommandAcceptor<>(context);
    }

    @Override
    public int deleteById(K key) {
        AssertUtil.notNull(key, "delete key required");

        AbstractQueryRequest<K, V> queryRequest = QueryRequestBuilder
                .bind(DeleteByIdQueryRequest.class, context)
                .create(Collections.singletonList(key));
        return queryExecuteRequestCommandAcceptor.executeAndGetAffect(queryRequest, DeleteByIdSqlExecutor.class);
    }

    @Override
    public int deleteByIds(Collection<K> keys) {
        AssertUtil.notEmpty(keys, "delete keys required");

        AbstractQueryRequest<K, V> queryRequest = QueryRequestBuilder
                .bind(DeleteByIdQueryRequest.class, context)
                .create(new ArrayList<>(keys));

        return queryExecuteRequestCommandAcceptor.executeAndGetAffect(queryRequest, DeleteByIdSqlExecutor.class);
    }

    @Override
    public int delete(Collection<V> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }

        EntityTableField primaryKeyField = context.getEntityTable().getPrimaryKeyField();
        Field keyField = primaryKeyField.getJavaField();

        Collection<K> entityKeys = EntityUtil.readKeys(entities, keyField);

        if (entityKeys.isEmpty()) {
            return 0;
        }

        return deleteByIds(entityKeys);
    }

    @Override
    public int delete() {
        AssertUtil.isTrue(!context.localQueryWhere().isEmpty(), "delete where condition required");

        AbstractQueryRequest<K, V> queryRequest = QueryRequestBuilder
                .bind(DeleteByWhereQueryRequest.class, context)
                .create(LimitHandler.empty(), SortHandler.empty());

        queryRequest.setQueryWhereHandler(getWhereHandler());

        return queryExecuteRequestCommandAcceptor.executeAndGetAffect(queryRequest, DeleteByWhereSqlExecutor.class);
    }
}
