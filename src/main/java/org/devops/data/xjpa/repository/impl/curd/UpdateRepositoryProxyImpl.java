package org.devops.data.xjpa.repository.impl.curd;

import lombok.extern.slf4j.Slf4j;
import org.devops.data.xjpa.repository.IUpdateRepository;
import org.devops.data.xjpa.repository.UpdateOperator;
import org.devops.data.xjpa.repository.UpdateRequest;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.repository.impl.RepositoryContextBean;
import org.devops.data.xjpa.repository.impl.enhance.EnhanceCurdBound;
import org.devops.data.xjpa.sql.executor.MultipleColumnUpdateValueHandler;
import org.devops.data.xjpa.sql.executor.SingleColumnUpdateValueHandler;
import org.devops.data.xjpa.sql.executor.UpdateByEntitySqlExecutor;
import org.devops.data.xjpa.sql.executor.UpdateByWhereSqlExecutor;
import org.devops.data.xjpa.sql.executor.UpdateValueHandler;
import org.devops.data.xjpa.sql.executor.command.DefaultQueryExecuteRequestCommandAcceptor;
import org.devops.data.xjpa.sql.executor.command.QueryExecuteRequestCommandAcceptor;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.query.QueryRequestBuilder;
import org.devops.data.xjpa.sql.executor.query.UpdateByEntityQueryRequest;
import org.devops.data.xjpa.sql.executor.query.UpdateByWhereQueryRequest;
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
@Slf4j
public class UpdateRepositoryProxyImpl<K extends Serializable,V> extends RepositoryContextBean<K ,V> implements IUpdateRepository<K, V> {

    private final QueryExecuteRequestCommandAcceptor queryExecuteRequestCommandAcceptor;

    private final EnhanceCurdBound enhanceCurdBound;

    protected UpdateRepositoryProxyImpl(RepositoryContext<K ,V> context, EnhanceCurdBound enhanceCurdBound) {
        super(context);
        this.queryExecuteRequestCommandAcceptor = new DefaultQueryExecuteRequestCommandAcceptor<>(context);
        this.enhanceCurdBound = enhanceCurdBound;
    }

    @Override
    public int update(String targetColumn, String operateColumn, Object operateValue, UpdateOperator updateOperator) {
        UpdateValueHandler updateValueHandler = SingleColumnUpdateValueHandler.builder()
                .operatorColumn(operateColumn)
                .targetColumn(targetColumn)
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
    public int add(String targetColumn, Object operateValue) {
        return update(targetColumn, null, operateValue, UpdateOperator.ADD);
    }

    @Override
    public int add(String targetColumn, String operateColumn, Object operateValue) {
        return update(targetColumn, operateColumn, operateValue, UpdateOperator.ADD);
    }

    @Override
    public int subtract(String targetColumn, Object operateValue) {
        return update(targetColumn, null, operateValue, UpdateOperator.SUB);
    }

    @Override
    public int subtract(String targetColumn, String operateColumn, Object operateValue) {
        return update(targetColumn, operateColumn, operateValue, UpdateOperator.SUB);
    }

    @Override
    public int multiply(String targetColumn, Object operateValue) {
        return update(targetColumn, null, operateValue, UpdateOperator.MCL);
    }

    @Override
    public int multiply(String targetColumn, String operateColumn, Object operateValue) {
        return update(targetColumn, operateColumn, operateValue, UpdateOperator.MCL);
    }

    @Override
    public int divide(String targetColumn, Object operateValue) {
        return update(targetColumn, null, operateValue, UpdateOperator.DIV);
    }

    @Override
    public int divide(String targetColumn, String operateColumn, Object operateValue) {
        return update(targetColumn, operateColumn, operateValue, UpdateOperator.DIV);
    }
}
