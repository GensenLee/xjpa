package com.glee.xjpa.repository.impl.curd;

import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.repository.impl.RepositoryContextBean;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;
import com.glee.xjpa.repository.IStandardSelectRepository;
import com.glee.xjpa.sql.executor.SelectSqlExecutor;
import com.glee.xjpa.sql.executor.command.QueryExecuteRequestCommandAcceptor;
import com.glee.xjpa.sql.executor.command.SingleColumnQueryExecuteRequestCommandAcceptor;
import com.glee.xjpa.sql.executor.query.AbstractQueryRequest;
import com.glee.xjpa.sql.executor.query.QueryRequestBuilder;
import com.glee.xjpa.sql.executor.query.SelectQueryRequest;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description SingleColumnSupport实现
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SingleColumnSupportSelectRepositoryProxyImpl<K extends Serializable, V> extends RepositoryContextBean<K, V>
        implements IStandardSelectRepository<K, V> {

    private final QueryExecuteRequestCommandAcceptor queryExecuteRequestCommandAcceptor;

    private final SelectRepositoryProxyImpl<K, V> selectRepositoryProxy;

    protected SingleColumnSupportSelectRepositoryProxyImpl(RepositoryContext<K, V> context, EnhanceCurdBound enhanceCurdBound) {
        super(context);
        this.selectRepositoryProxy = new SelectRepositoryProxyImpl<>(context, enhanceCurdBound);
        this.queryExecuteRequestCommandAcceptor = new SingleColumnQueryExecuteRequestCommandAcceptor<>(context);
    }

    //<editor-fold desc="组合代理">
    @Override
    public List<V> list() {
        return selectRepositoryProxy.list();
    }

    @Override
    public List<V> listByIds(Collection<K> keys) {
        return selectRepositoryProxy.listByIds(keys);
    }

    @Override
    public <T> List<T> list(Class<T> resultType) {
        return selectRepositoryProxy.list(resultType);
    }

    @Override
    public V get() {
        return selectRepositoryProxy.get();
    }

    @Override
    public V getById(K key) {
        return selectRepositoryProxy.getById(key);
    }

    @Override
    public <T> T get(Class<T> resultType) {
        return selectRepositoryProxy.get(resultType);
    }

    @Override
    public boolean isExists() {
        return selectRepositoryProxy.isExists();
    }

    @Override
    public boolean isExistsById(K key) {
        return selectRepositoryProxy.isExistsById(key);
    }

    @Override
    public long count() {
        return selectRepositoryProxy.count();
    }

    //</editor-fold>




    @Override
    public <T> List<T> listSingleColumn(Class<T> clazz) {
        Assert.notNull(clazz, "result type required");
        EnhanceCurdBound enhanceCurdBound = getContext().getSingleton(EnhanceCurdBound.class);

        Collection<String> includeColumns = enhanceCurdBound.getIncludeColumns();
        Collection<String> distinctColumns = enhanceCurdBound.getDistinctColumns();

        Assert.isTrue(CollectionUtils.isEmpty(distinctColumns) || CollectionUtils.isEmpty(includeColumns),
                "can not use #distinct and #include at the same time");

        Assert.isTrue(!CollectionUtils.isEmpty(distinctColumns) || !CollectionUtils.isEmpty(includeColumns),
                "please specify a column use #distinct or #include");

        boolean distinct = !CollectionUtils.isEmpty(distinctColumns);
        if (distinct) {
            Assert.isTrue(distinctColumns.size() == 1, "distinct column length must be 1");
        }else {
            Assert.isTrue(includeColumns.size() == 1, "include column length must be 1");
        }


        AbstractQueryRequest<K, V> selectQueryRequest = QueryRequestBuilder
                .bind(SelectQueryRequest.class, context)
                .create(enhanceCurdBound.getLimitHandler(), enhanceCurdBound.getSortHandler(),
                        (distinct ? distinctColumns : includeColumns),
                        distinct, enhanceCurdBound.getGroupingColumns(),
                        enhanceCurdBound.getHavingString());

        selectQueryRequest.setQueryWhereHandler(getWhereHandler());

        return queryExecuteRequestCommandAcceptor.executeAndConvert(selectQueryRequest, SelectSqlExecutor.class, clazz);
    }

    @Override
    public <T> T getSingleColumn(Class<T> clazz) {
        return CollectionUtils.firstElement(listSingleColumn(clazz));
    }


}
