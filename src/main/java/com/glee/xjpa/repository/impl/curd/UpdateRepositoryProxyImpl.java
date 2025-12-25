package com.glee.xjpa.repository.impl.curd;

import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.repository.impl.RepositoryContextBean;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;
import com.glee.xjpa.repository.IUpdateRepository;
import com.glee.xjpa.repository.UpdateOperator;
import com.glee.xjpa.repository.UpdateRequest;
import com.glee.xjpa.sql.executor.MultipleColumnUpdateValueHandler;
import com.glee.xjpa.sql.executor.SingleColumnUpdateValueHandler;
import com.glee.xjpa.sql.executor.UpdateByEntitySqlExecutor;
import com.glee.xjpa.sql.executor.UpdateByWhereSqlExecutor;
import com.glee.xjpa.sql.executor.UpdateValueHandler;
import com.glee.xjpa.sql.executor.command.DefaultQueryExecuteRequestCommandAcceptor;
import com.glee.xjpa.sql.executor.command.QueryExecuteRequestCommandAcceptor;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.executor.query.QueryRequestBuilder;
import com.glee.xjpa.sql.executor.query.UpdateByEntityQueryRequest;
import com.glee.xjpa.sql.executor.query.UpdateByWhereQueryRequest;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description update代理实现
 */
@SuppressWarnings({"rawtypes"})
public class UpdateRepositoryProxyImpl<K extends Serializable,V> extends RepositoryContextBean<K ,V> implements IUpdateRepository<K, V> {

    private final QueryExecuteRequestCommandAcceptor queryExecuteRequestCommandAcceptor;

    private final EnhanceCurdBound enhanceCurdBound;

    protected UpdateRepositoryProxyImpl(RepositoryContext<K ,V> context, EnhanceCurdBound enhanceCurdBound) {
        super(context);
        this.queryExecuteRequestCommandAcceptor = new DefaultQueryExecuteRequestCommandAcceptor<>(context);
        this.enhanceCurdBound = enhanceCurdBound;
    }

    @Override
    public int update(String column, String operateColumn, Object operateValue, UpdateOperator updateOperator) {
        UpdateValueHandler updateValueHandler = SingleColumnUpdateValueHandler.builder()
                .operatorColumn(operateColumn)
                .targetColumn(column)
                .value(operateValue)
                .updateOperator(updateOperator)
                .build();


        return doUpdate(updateValueHandler);
    }

    /**
     * @param updateValueHandler
     * @return
     */
    private int doUpdate(UpdateValueHandler updateValueHandler) {
        AbstractQueryRequest<K, V> queryRequest = QueryRequestBuilder
                .bind(UpdateByWhereQueryRequest.class, context)
                .create(enhanceCurdBound.getLimitHandler(), enhanceCurdBound.getSortHandler(), updateValueHandler);

        queryRequest.setQueryWhereHandler(getWhereHandler());
        return queryExecuteRequestCommandAcceptor.executeAndGetAffect(queryRequest, UpdateByWhereSqlExecutor.class);
    }

    @Override
    public int update(UpdateRequest updateRequest) {
        if (CollectionUtils.isEmpty(updateRequest.getUpdateColumns())) {
            throw new IllegalArgumentException("empty update request");
        }

        UpdateValueHandler updateValueHandler = new MultipleColumnUpdateValueHandler(updateRequest.getUpdateColumns());

        return doUpdate(updateValueHandler);
    }

    @Override
    public int update(V entity) {
        return update(Collections.singletonList(entity));
    }

    @Override
    public int update(Collection<V> entities) {

        AbstractQueryRequest<K, V> queryRequest = QueryRequestBuilder
                .bind(UpdateByEntityQueryRequest.class, context)
                .create(entities);

        return queryExecuteRequestCommandAcceptor.executeAndGetAffect(queryRequest, UpdateByEntitySqlExecutor.class);
    }




    @Override
    public int add(String column, Object operateValue) {
        return update(column, null, operateValue, UpdateOperator.ADD);
    }

    @Override
    public int add(String column, String operateColumn, Object operateValue) {
        return update(column, operateColumn, operateValue, UpdateOperator.ADD);
    }

    @Override
    public int subtract(String column, Object operateValue) {
        return update(column, null, operateValue, UpdateOperator.SUB);
    }

    @Override
    public int subtract(String column, String operateColumn, Object operateValue) {
        return update(column, operateColumn, operateValue, UpdateOperator.SUB);
    }

    @Override
    public int multiply(String column, Object operateValue) {
        return update(column, null, operateValue, UpdateOperator.MCL);
    }

    @Override
    public int multiply(String column, String operateColumn, Object operateValue) {
        return update(column, operateColumn, operateValue, UpdateOperator.MCL);
    }

    @Override
    public int divide(String column, Object operateValue) {
        return update(column, null, operateValue, UpdateOperator.DIV);
    }

    @Override
    public int divide(String column, String operateColumn, Object operateValue) {
        return update(column, operateColumn, operateValue, UpdateOperator.DIV);
    }
}
