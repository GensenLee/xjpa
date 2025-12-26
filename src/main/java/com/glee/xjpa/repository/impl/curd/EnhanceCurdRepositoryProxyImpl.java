package com.glee.xjpa.repository.impl.curd;

import cn.hutool.core.bean.BeanUtil;
import com.glee.xjpa.exception.XjpaExecuteException;
import com.glee.xjpa.repository.*;
import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.repository.impl.RepositoryContextBean;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;
import com.glee.xjpa.repository.impl.enhance.ThreadLocalEnhanceCurdBound;
import com.glee.xjpa.sql.executor.SortType;
import com.glee.xjpa.table.EntityTable;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.util.EntityUtil;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description curd代理实现类
 */
@SuppressWarnings({"rawtypes"})
public class EnhanceCurdRepositoryProxyImpl<K extends Serializable, V> extends RepositoryContextBean<K, V> implements IEnhanceCurdRepository<K, V> {

    private final EnhanceCurdBound enhanceCurdBound;

    private final IStandardSelectRepository<K, V> selectRepository;

    private final IInsertRepository<K, V> insertRepository;

    private final IUpdateRepository<K, V> updateRepository;

    private final IDeleteRepository<K, V> deleteRepository;

    public EnhanceCurdRepositoryProxyImpl(RepositoryContext<K, V> context,
                                          EnhanceCurdBound enhanceCurdBound,
                                          IStandardSelectRepository<K, V> selectRepository,
                                          IInsertRepository<K, V> insertRepository,
                                          IUpdateRepository<K, V> updateRepository,
                                          IDeleteRepository<K, V> deleteRepository) {
        super(context);
        this.enhanceCurdBound = enhanceCurdBound;
        this.selectRepository = selectRepository;
        this.insertRepository = insertRepository;
        this.updateRepository = updateRepository;
        this.deleteRepository = deleteRepository;
    }

    @Override
    public int deleteById(K key) {
        if (!isExistsById(key)) {
            return 0;
        }
        return deleteRepository.deleteById(key);
    }

    @Override
    public int deleteByIds(Collection<K> keys) {
        EntityTable<K, V> entityTable = getContext().getEntityTable();

        include(entityTable.pickTableField().getTableFieldMetadata().getField());
        List<V> list = listByIds(keys);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }


        List<K> deleteKeys = list.stream()
                .map(entityTable::getPrimaryKeyFieldValue)
                .collect(Collectors.toList());

        return deleteRepository.deleteByIds(deleteKeys);
    }

    @Override
    public int delete(Collection<V> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }

        EntityTableField primaryKeyField = context.getEntityTable().getPrimaryKeyField();
        Field javaField = primaryKeyField.getJavaField();

        Set<V> deleteSet = entities.stream()
                .filter(Objects::nonNull)
                .filter(e -> BeanUtil.getFieldValue(e, javaField.getName()) != null)
                .collect(Collectors.toSet());

        return deleteRepository.delete(deleteSet);
    }

    @Override
    public int delete() {
        EntityTable<K, V> entityTable = getContext().getEntityTable();

        include(entityTable.pickTableField().getTableFieldMetadata().getField());
        List<K> keys = listSingleColumn(context.getEntityTable().getKeyType());
        if (CollectionUtils.isEmpty(keys)) {
            return 0;
        }

        return deleteRepository.deleteByIds(keys);
    }

    @Override
    public int insert(V entity) {
        return insertRepository.insert(entity);
    }

    @Override
    public int insert(Collection<V> entities) {
        return insertRepository.insert(entities);
    }

    @Override
    public List<V> list() {
        return selectRepository.list();
    }

    @Override
    public List<V> listByIds(Collection<K> keys) {
        return selectRepository.listByIds(keys);
    }

    @Override
    public <T> List<T> list(Class<T> resultType) {
        return selectRepository.list(resultType);
    }

    @Override
    public V get() {
        limit(1);
        return selectRepository.get();
    }

    @Override
    public V getById(K key) {
        limit(1);
        return selectRepository.getById(key);
    }

    @Override
    public <T> T get(Class<T> resultType) {
        limit(1);
        return selectRepository.get(resultType);
    }

    @Override
    public <T> List<T> listSingleColumn(Class<T> clazz) {
        return selectRepository.listSingleColumn(clazz);
    }

    @Override
    public <T> T getSingleColumn(Class<T> clazz) {
        limit(1);
        return selectRepository.getSingleColumn(clazz);
    }

    @Override
    public IEnhanceCurdRepository<K, V> ignoreSoftDelete() {
        enhanceCurdBound.ignoreSoftDelete();
        return this;
    }

    @Override
    public IEnhanceCurdRepository<K, V> groupByColumns(String... columns) {
        enhanceCurdBound.groupByColumns(columns);
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> having(String havingString) {
        enhanceCurdBound.having(havingString);
        return this;
    }

    @Override
    public boolean isExists() {
        return selectRepository.isExists();
    }

    @Override
    public boolean isExistsById(K key) {
        return selectRepository.isExistsById(key);
    }

    @Override
    public long count() {
        return selectRepository.count();
    }

    @Override
    public IEnhanceCurdRepository<K, V> distinct(String... columns) {
        enhanceCurdBound.distinct(columns);
        return this;
    }

    @Override
    public int update(String column, String operateColumn, Object operateValue, UpdateOperator updateOperator) {
        return updateRepository.update(column, operateColumn, operateValue, updateOperator);
    }

    @Override
    public int update(UpdateRequest updateRequest) {
        return updateRepository.update(updateRequest);
    }

    @Override
    public int update(V entity) {
        return update(Collections.singletonList(entity));
    }

    @Override
    public int update(Collection<V> entities) {

        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }
        List<V> needUpdate = filterNotNeedUpdate(entities);

        if (CollectionUtils.isEmpty(needUpdate)) {
            return 0;
        }

        return updateRepository.update(needUpdate);
    }

    @Override
    public int add(String column, Object operateValue) {
        return updateRepository.add(column, operateValue);
    }

    @Override
    public int add(String column, String operateColumn, Object operateValue) {
        return updateRepository.add(column, operateColumn, operateValue);
    }

    @Override
    public int subtract(String column, Object operateValue) {
        return updateRepository.subtract(column, operateValue);
    }

    @Override
    public int subtract(String column, String operateColumn, Object operateValue) {
        return updateRepository.subtract(column, operateColumn, operateValue);
    }

    @Override
    public int multiply(String column, Object operateValue) {
        return updateRepository.multiply(column, operateValue);
    }

    @Override
    public int multiply(String column, String operateColumn, Object operateValue) {
        return updateRepository.multiply(column, operateColumn, operateValue);
    }

    @Override
    public int divide(String column, Object operateValue) {
        return updateRepository.divide(column, operateValue);
    }

    @Override
    public int divide(String column, String operateColumn, Object operateValue) {
        return updateRepository.divide(column, operateColumn, operateValue);
    }

    @Override
    public IEnhanceCurdRepository<K, V> include(String... columns) {
        enhanceCurdBound.include(columns);
        return this;
    }

    @Override
    public IEnhanceCurdRepository<K, V> exclude(String... columns) {
        enhanceCurdBound.exclude(columns);
        return this;
    }

    @Override
    public IEnhanceCurdRepository<K, V> limit(int size) {
        enhanceCurdBound.limit(size);
        return this;
    }

    @Override
    public IEnhanceCurdRepository<K, V> limit(int start, int size) {
        enhanceCurdBound.limit(start, size);
        return this;
    }

    @Override
    public IEnhanceRepository<K, V> limit(boolean isPage, int start, int size) {
        enhanceCurdBound.limit(isPage, start, size);
        return this;
    }

    @Override
    public IEnhanceCurdRepository<K, V> orderString(String orderByString) {
        enhanceCurdBound.orderString(orderByString);
        return this;
    }

    @Override
    public IEnhanceCurdRepository<K, V> orderByColumn(String column, SortType sortType) {
        enhanceCurdBound.orderByColumn(column, sortType);
        return this;
    }

    @Override
    public IEnhanceCurdRepository<K, V> descByColumn(String column) {
        orderByColumn(column, SortType.desc);
        return this;
    }

    @Override
    public IEnhanceCurdRepository<K, V> ascByColumn(String column) {
        orderByColumn(column, SortType.asc);
        return this;
    }

    /**
     * 过滤掉不需要要改update的记录，
     * @param entities
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<V> filterNotNeedUpdate(Collection<V> entities) {
        EntityTable<K, V> entityTable = context.getEntityTable();
        Field keyField = entityTable
                .getPrimaryKeyField()
                .getJavaField();
        Collection<K> entityKeys = EntityUtil.readKeys(entities, keyField);
        if (entityKeys.size() != entities.size()) {
            throw new XjpaExecuteException("update key must be specified");
        }

        Collection<String> includeColumns = enhanceCurdBound.getIncludeColumns();
        if (!includeColumns.isEmpty()) {
            ((ThreadLocalEnhanceCurdBound) enhanceCurdBound).setSystemIncludes(entityTable.getPrimaryKeyField().getTableFieldMetadata().getField());
        }

        List<V> entityListFromDb = selectRepository.listByIds(entityKeys);
        if (CollectionUtils.isEmpty(entityListFromDb)) {
            return Collections.emptyList();
        }
        Map<Object, V> entityFromDbMap = entityListFromDb.stream()
                .collect(Collectors.toMap(e -> BeanUtil.getFieldValue(e, keyField.getName()), Function.identity()));

        return entities.stream()
                .filter(e -> !noChange(entityFromDbMap.get(BeanUtil.getFieldValue(e, keyField.getName())), e, entityTable, includeColumns))
                .collect(Collectors.toList());
    }

    /**
     * 检查是否有变更
     * @param entityFromDb
     * @param entityToUpdate
     * @param entityTable
     * @param includeColumns
     * @return
     */
    private boolean noChange(V entityFromDb, V entityToUpdate, EntityTable<K, V> entityTable, Collection includeColumns) {
        // 比较是否全部非空值都没有变更
        for (EntityTableField entityTableField : entityTable.getEntityTableFieldList()) {
            if (entityTableField.isPriKey()) {
                continue;
            }

            if (!CollectionUtils.isEmpty(includeColumns) && !includeColumns.contains(entityTableField.getTableFieldMetadata().getField())) {
                continue;
            }

            Object valueToUpdate = BeanUtil.getFieldValue(entityToUpdate, entityTableField.getJavaField().getName());
            if (valueToUpdate == null) {
                continue;
            }
            Object valueFromDb = BeanUtil.getFieldValue(entityFromDb, entityTableField.getJavaField().getName());
            if (!valueToUpdate.equals(valueFromDb)) {
                return false;
            }
        }
        return true;
    }

}
