package com.glee.xjpa.repository.impl.curd;

import com.glee.xjpa.sql.where.handler.DefaultQueryWhereHandler;
import com.glee.xjpa.sql.where.handler.IQueryWhereHandler;
import com.glee.xjpa.sql.where.handler.SoftDeleteConfig;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import com.glee.xjpa.sql.where.operate.WhereOperator;
import com.glee.xjpa.repository.IDeleteRepository;
import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.repository.impl.RepositoryContextBean;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;
import com.glee.xjpa.sql.executor.SingleColumnUpdateValueHandler;
import com.glee.xjpa.sql.executor.UpdateByWhereSqlExecutor;
import com.glee.xjpa.sql.executor.UpdateValueHandler;
import com.glee.xjpa.sql.executor.command.DefaultQueryExecuteRequestCommandAcceptor;
import com.glee.xjpa.sql.executor.command.QueryExecuteRequestCommandAcceptor;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.executor.query.QueryRequestBuilder;
import com.glee.xjpa.sql.executor.query.UpdateByWhereQueryRequest;
import com.glee.xjpa.sql.where.usermodel.XQueryWhereValue;
import com.glee.xjpa.sql.where.usermodel.XQueryWhereValues;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.util.EntityUtil;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author GENSEN
 * @date 2022-12-02
 * @description 逻辑删除soft delete代理实现
 */
public class SoftDeleteRepositoryProxyImpl<K extends Serializable, V> extends RepositoryContextBean<K, V> implements IDeleteRepository<K, V> {

    private final QueryExecuteRequestCommandAcceptor queryExecuteRequestCommandAcceptor;

    private final EnhanceCurdBound<K, V> enhanceCurdBound;

    private final SoftDeleteConfig softDeleteConfig;

    protected SoftDeleteRepositoryProxyImpl(RepositoryContext<K, V> context, EnhanceCurdBound<K, V> enhanceCurdBound,
                                            SoftDeleteConfig softDeleteConfig) {
        super(context);
        this.enhanceCurdBound = enhanceCurdBound;
        this.softDeleteConfig = softDeleteConfig;
        this.queryExecuteRequestCommandAcceptor = new DefaultQueryExecuteRequestCommandAcceptor<>(context);
    }

    @Override
    public int deleteById(K key) {
        Assert.notNull(key, "delete key required");

        return deleteByIds(Collections.singletonList(key));
    }

    @Override
    public int deleteByIds(Collection<K> keys) {
        Assert.notEmpty(keys, "delete keys required");

        EntityTableField primaryKeyField = context.getEntityTable().getPrimaryKeyField();

        IQueryWhereObject deleteIdWhere = new XQueryWhereValue(primaryKeyField.getTableFieldMetadata().getField(), keys, WhereOperator.IN);
        IQueryWhereObject softDeleteControlWhere = new XQueryWhereValue(softDeleteConfig.getColumn(), softDeleteConfig.getNotDeleteValue());

        IQueryWhereHandler whereHandler = new DefaultQueryWhereHandler(new XQueryWhereValues(
                Arrays.asList(deleteIdWhere, softDeleteControlWhere)
        ));

        UpdateValueHandler updateValueHandler = SingleColumnUpdateValueHandler.builder()
                .targetColumn(softDeleteConfig.getColumn())
                .value(softDeleteConfig.getDeletedValue())
                .build();
        return doUpdate(updateValueHandler, whereHandler);
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
        Assert.isTrue(!context.localQueryWhere().isEmpty(), "delete where condition required");

        UpdateValueHandler updateValueHandler = SingleColumnUpdateValueHandler.builder()
                .targetColumn(softDeleteConfig.getColumn())
                .value(softDeleteConfig.getDeletedValue())
                .build();

        return doUpdate(updateValueHandler, getWhereHandler());
    }



    /**
     * @param updateValueHandler
     * @param queryWhereHandler
     * @return
     */
    private int doUpdate(UpdateValueHandler updateValueHandler, IQueryWhereHandler queryWhereHandler) {
        AbstractQueryRequest<K, V> queryRequest = QueryRequestBuilder
                .bind(UpdateByWhereQueryRequest.class, context)
                .create(enhanceCurdBound.getLimitHandler(), enhanceCurdBound.getSortHandler(), updateValueHandler);

        queryRequest.setQueryWhereHandler(queryWhereHandler);
        return queryExecuteRequestCommandAcceptor.executeAndGetAffect(queryRequest, UpdateByWhereSqlExecutor.class);
    }

}
