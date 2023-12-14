package org.devops.data.xjpa.repository.impl.curd;

import org.devops.core.utils.util.AssertUtil;
import org.devops.data.xjpa.repository.IInsertRepository;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.repository.impl.RepositoryContextBean;
import org.devops.data.xjpa.sql.executor.AbstractSqlExecutor;
import org.devops.data.xjpa.sql.executor.ConfigurableMultipleValueInsertSqlExecutor;
import org.devops.data.xjpa.sql.executor.SingleValueInsertSqlExecutor;
import org.devops.data.xjpa.sql.executor.command.DefaultQueryExecuteRequestCommandAcceptor;
import org.devops.data.xjpa.sql.executor.command.QueryExecuteRequestCommandAcceptor;
import org.devops.data.xjpa.sql.executor.key.ContextEntityPrimaryKeyHandler;
import org.devops.data.xjpa.sql.executor.key.EntityPrimaryKeyHandler;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.query.InsertQueryRequest;
import org.devops.data.xjpa.sql.executor.query.QueryRequestBuilder;
import org.devops.data.xjpa.table.EntityTable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description insert代理实现
 */
@SuppressWarnings({"rawtypes"})
public class InsertRepositoryProxyImpl<K extends Serializable, V> extends RepositoryContextBean<K, V> implements IInsertRepository<K, V> {

    private final QueryExecuteRequestCommandAcceptor queryExecuteRequestCommandAcceptor;

    protected InsertRepositoryProxyImpl(RepositoryContext<K, V> context) {
        super(context);
        this.queryExecuteRequestCommandAcceptor = new DefaultQueryExecuteRequestCommandAcceptor<>(context);
    }

    @Override
    public int insert(V entity) {
        if (entity == null) {
            return 0;
        }
        return doInsert(Collections.singletonList(entity), SingleValueInsertSqlExecutor.class);
    }

    @Override
    public int insert(Collection<V> entities) {
        AssertUtil.notEmpty(entities, "empty insert list");


        EntityTable<K, V> entityTable = getContext().getEntityTable();

        return doInsert(entities, SingleValueInsertSqlExecutor.class);

        // 不同对象不同字段、主键读取 等问题未解决
/*        if (entities.size() == 1) {
            // 单条插入
            return doInsert(entities, SingleValueInsertSqlExecutor.class);
        }

        if (entityTable.getPrimaryKeyField() == null || !entityTable.isAutoincrement()) {
            // 无主键，或非自增主键，使用单条语句多值插入
            return doInsert(entities, ConfigurableMultipleValueInsertSqlExecutor.class);
        }

        // 自增主键，插入列表主键，主键为null 和 not null 切分为两个列表插入，以保证正常获取数据库自增id
        Map<Boolean, List<V>> partitions = entities.stream().collect(Collectors.partitioningBy(e -> entityTable.getPrimaryKeyFieldValue(e) == null));

        int insertedRows = 0;

        if (!partitions.get(false).isEmpty()) {
            insertedRows += doInsert(partitions.get(false), ConfigurableMultipleValueInsertSqlExecutor.class);
        }

        getContext().setAttribute(ConfigurableMultipleValueInsertSqlExecutor.ignoreGenerateKeyName, Boolean.TRUE.toString());

        if (!partitions.get(true).isEmpty()) {
            insertedRows += doInsert(partitions.get(true), ConfigurableMultipleValueInsertSqlExecutor.class);
        }

        return insertedRows;*/
    }

    /**
     * @param entities
     * @param executorType
     * @return
     */
    private int doInsert(Collection<V> entities, Class<? extends AbstractSqlExecutor> executorType) {
        AbstractQueryRequest<K, V> queryRequest = QueryRequestBuilder
                .bind(InsertQueryRequest.class, context)
                .create(entities);

        EntityPrimaryKeyHandler entityPrimaryKeyHandler = new ContextEntityPrimaryKeyHandler<>(entities, context);
        context.setAttribute(EntityPrimaryKeyHandler.class.getName(), entityPrimaryKeyHandler);

        return queryExecuteRequestCommandAcceptor.executeAndGetAffect(queryRequest, executorType);
    }
}
