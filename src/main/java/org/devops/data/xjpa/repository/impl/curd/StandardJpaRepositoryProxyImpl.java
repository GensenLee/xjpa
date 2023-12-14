package org.devops.data.xjpa.repository.impl.curd;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.devops.data.xjpa.annotation.SkipRepositoryScan;
import org.devops.data.xjpa.configuration.RepositoryProperties;
import org.devops.data.xjpa.join.DefaultJoiningTableRepository;
import org.devops.data.xjpa.join.JoinModel;
import org.devops.data.xjpa.join.JoiningTableRepository;
import org.devops.data.xjpa.lifecycle.Disposable;
import org.devops.data.xjpa.repository.*;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.repository.impl.RepositoryContextAttribute;
import org.devops.data.xjpa.repository.impl.RepositoryContextObserver;
import org.devops.data.xjpa.sql.executor.SortType;
import org.devops.data.xjpa.sql.executor.session.DataSourceExecuteSession;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.sql.result.parser.ResultParser;
import org.devops.data.xjpa.sql.where.XQueryWhere;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;
import org.devops.data.xjpa.table.EntityTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description Repository代理实现
 */
@Slf4j
@Setter(AccessLevel.PACKAGE)
@SkipRepositoryScan
public class StandardJpaRepositoryProxyImpl<K extends Serializable, V> implements StandardJpaRepository<K, V>, RepositoryContext<K, V> {

    @Getter
    private final Class<K> keyType;
    @Getter
    private final Class<V> entityType;

    private final SqlLogger sqlLogger;

    private final RepositoryProperties repositoryProperties;

    private final ResultParser resultParser;

    private IEnhanceCurdRepository<K, V> enhanceCurdRepository;

    private final ExecuteSession executeSession;

    private final List<RepositoryContextObserver> observerList = new ArrayList<>();

    private final RepositoryContextAttribute repositoryContextAttribute;

    private final RepositoryWhereLocal<K, V> repositoryWhereLocal = new RepositoryWhereLocal<>();

    private final JoiningTableRepository joiningTableRepository;


    public StandardJpaRepositoryProxyImpl(Class<K> keyType, Class<V> entityType,
                                          SqlLogger sqlLogger,
                                          RepositoryProperties repositoryProperties,
                                          ResultParser resultParser,
                                          RepositoryContextAttribute repositoryContextAttribute) {
        this.keyType = keyType;
        this.entityType = entityType;
        this.sqlLogger = sqlLogger;
        this.repositoryProperties = repositoryProperties;
        this.resultParser = resultParser;
        this.repositoryContextAttribute = repositoryContextAttribute;
        this.executeSession = new DataSourceExecuteSession(repositoryProperties.getDataSource());
        this.joiningTableRepository = new DefaultJoiningTableRepository(this);
    }

    @Override
    public XQueryWhere localQueryWhere() {
        return repositoryWhereLocal.localQueryWhere();
    }

    @Override
    public SqlLogger localSqlLogger() {
        return sqlLogger;
    }

    @Override
    public ExecuteSession localSessionManager() {
        return executeSession;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EntityTable<K, V> getEntityTable() {
        return repositoryProperties.getEntityTable();
    }

    @Override
    public ResultParser getResultParser() {
        return resultParser;
    }

    @Override
    public void dispose() {
        // 清空当前次操作过程数据

        log.trace("context[{}] dispose", getEntityTable().getTableName());

        repositoryWhereLocal.clearLocalQueryWhere();
        if (executeSession != null) {
            executeSession.dispose();
        }

        if (sqlLogger instanceof Disposable) {
            ((Disposable) sqlLogger).dispose();
        }

        notifyDispose();
    }

    @Override
    public void close() {

        // 应用停止时
        repositoryWhereLocal.clearLocalQueryWhere();

        if (executeSession != null) {
            executeSession.dispose();
        }

        if (sqlLogger instanceof Disposable) {
            ((Disposable) sqlLogger).dispose();
        }

        notifyClose();
    }


    //<editor-fold desc="curd">
    @Override
    public int deleteById(K key) {
        return enhanceCurdRepository.deleteById(key);
    }

    @Override
    public int deleteByIds(Collection<K> keys) {
        return enhanceCurdRepository.deleteByIds(keys);
    }

    @Override
    public int delete(Collection<V> entities) {
        return enhanceCurdRepository.delete(entities);
    }

    @Override
    public int delete() {
        if (repositoryWhereLocal.localQueryWhere().isEmpty()) {
            throw new UnsupportedOperationException("delete condition required");
        }
        return enhanceCurdRepository.delete();
    }

    @Override
    public boolean isExists() {
        return enhanceCurdRepository.isExists();
    }

    @Override
    public boolean isExistsById(K key) {
        return enhanceCurdRepository.isExistsById(key);
    }

    @Override
    public long count() {
        return enhanceCurdRepository.count();
    }

    @Override
    public StandardJpaRepository<K, V> include(String... columns) {
        enhanceCurdRepository.include(columns);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> exclude(String... columns) {
        enhanceCurdRepository.exclude(columns);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> limit(int size) {
        enhanceCurdRepository.limit(size);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> limit(int start, int size) {
        enhanceCurdRepository.limit(start, size);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> limit(boolean isPage, int start, int size) {
        enhanceCurdRepository.limit(start, size);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> distinct(String... columns) {
        enhanceCurdRepository.distinct(columns);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> ignoreSoftDelete() {
        enhanceCurdRepository.ignoreSoftDelete();
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> groupByColumns(String... columns) {
        enhanceCurdRepository.groupByColumns(columns);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> having(String havingString) {
        enhanceCurdRepository.having(havingString);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> orderString(String orderByString) {
        enhanceCurdRepository.orderString(orderByString);
        return this;
    }

    @Override
    public StandardJpaRepository<K, V> orderByColumn(String column, SortType sortType) {
        enhanceCurdRepository.orderByColumn(column, sortType);
        return this;
    }

    @Override
    public List<V> list() {
        return enhanceCurdRepository.list();
    }

    @Override
    public List<V> listByIds(Collection<K> keys) {
        return enhanceCurdRepository.listByIds(keys);
    }

    @Override
    public <T> List<T> list(Class<T> resultType) {
        return enhanceCurdRepository.list(resultType);
    }

    @Override
    public V get() {
        return enhanceCurdRepository.get();
    }

    @Override
    public V getById(K key) {
        return enhanceCurdRepository.getById(key);
    }

    @Override
    public <T> T get(Class<T> resultType) {
        return enhanceCurdRepository.get(resultType);
    }

    @Override
    public <T> List<T> listSingleColumn(Class<T> clazz) {
        return enhanceCurdRepository.listSingleColumn(clazz);
    }

    @Override
    public <T> T getSingleColumn(Class<T> clazz) {
        return enhanceCurdRepository.getSingleColumn(clazz);
    }

    @Override
    public int update(String targetColumn, String operateColumn, Object operateValue, UpdateOperator updateOperator) {
        return enhanceCurdRepository.update(targetColumn, operateColumn, operateValue, updateOperator);
    }

    @Override
    public int update(UpdateRequest updateRequest) {
        return enhanceCurdRepository.update(updateRequest);
    }

    @Override
    public int update(V entity) {
        return enhanceCurdRepository.update(entity);
    }

    @Override
    public int update(Collection<V> entities) {
        return enhanceCurdRepository.update(entities);
    }

    @Override
    public int add(String targetColumn, Object operateValue) {
        return enhanceCurdRepository.add(targetColumn, operateValue);
    }

    @Override
    public int add(String targetColumn, String operateColumn, Object operateValue) {
        return enhanceCurdRepository.add(targetColumn, operateColumn, operateValue);
    }

    @Override
    public int subtract(String targetColumn, Object operateValue) {
        return enhanceCurdRepository.subtract(targetColumn, operateValue);
    }

    @Override
    public int subtract(String targetColumn, String operateColumn, Object operateValue) {
        return enhanceCurdRepository.subtract(targetColumn, operateColumn, operateValue);
    }

    @Override
    public int multiply(String targetColumn, Object operateValue) {
        return enhanceCurdRepository.multiply(targetColumn, operateValue);
    }

    @Override
    public int multiply(String targetColumn, String operateColumn, Object operateValue) {
        return enhanceCurdRepository.multiply(targetColumn, operateColumn, operateValue);
    }

    @Override
    public int divide(String targetColumn, Object operateValue) {
        return enhanceCurdRepository.divide(targetColumn, operateValue);
    }

    @Override
    public int divide(String targetColumn, String operateColumn, Object operateValue) {
        return enhanceCurdRepository.divide(targetColumn, operateColumn, operateValue);
    }

    @Override
    public int insert(V entities) {
        return enhanceCurdRepository.insert(entities);
    }

    @Override
    public int insert(Collection<V> entity) {
        return enhanceCurdRepository.insert(entity);
    }
    //</editor-fold>


    //<editor-fold desc="where">
    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(boolean valid, IQueryWhereObject whereValue) {
        repositoryWhereLocal.where(valid, whereValue);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(IQueryWhereObject whereValue) {
        repositoryWhereLocal.where(whereValue);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(String column, Object value) {
        repositoryWhereLocal.where(column, value);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(String column, WhereOperator operator) {
        repositoryWhereLocal.where(column, operator);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(String column, Object value, WhereOperator operator) {
        repositoryWhereLocal.where(column, value, operator);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(String column, WhereOperator operator, Condition condition) {
        repositoryWhereLocal.where(column, operator, condition);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(String column, Object value, Condition condition) {
        repositoryWhereLocal.where(column, value, condition);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(String column, Object value, WhereOperator operator, Condition condition) {
        repositoryWhereLocal.where(column, value, operator, condition);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(boolean valid, String column, Object value) {
        repositoryWhereLocal.where(valid, column, value);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(boolean valid, String column, WhereOperator operator) {
        repositoryWhereLocal.where(valid, column, operator);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(boolean valid, String column, WhereOperator operator, Condition condition) {
        repositoryWhereLocal.where(valid, column, operator, condition);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(boolean valid, String column, Object value, WhereOperator operator) {
        repositoryWhereLocal.where(valid, column, value, operator);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(boolean valid, String column, Object value, Condition condition) {
        repositoryWhereLocal.where(valid, column, value, condition);
        return this;
    }

    @Override
    public StandardJpaRepositoryProxyImpl<K, V> where(boolean valid, String column, Object value, WhereOperator operator, Condition condition) {
        repositoryWhereLocal.where(valid, column, value, operator, condition);
        return this;
    }

    @Override
    public void clear() {
        repositoryWhereLocal.clear();
    }
//</editor-fold>


    //<editor-fold desc="观察者">


    @Override
    public void register(RepositoryContextObserver observer) {
        if (observer == null) {
            return;
        }
        observerList.add(observer);
    }

    @Override
    public void notifyDispose() {
        for (RepositoryContextObserver observer : observerList) {
            observer.onDispose(this);
        }
    }

    @Override
    public void notifyClose() {
        for (RepositoryContextObserver observer : observerList) {
            observer.onClose(this);
        }
    }

    //</editor-fold>

    @Override
    public void setAttribute(String key, Object attribute) {
        repositoryContextAttribute.setAttribute(key, attribute);
    }

    @Override
    public Object getAttribute(String key) {
        return repositoryContextAttribute.getAttribute(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSingleton(Class<T> beanType) {
        Object attribute = getAttribute(beanType.getName());
        return (T) attribute;
    }

    @Override
    public Set<String> keySet() {
        return repositoryContextAttribute.keySet();
    }

    @Override
    public JoinModel leftJoin(Class<?> rightEntityType) {
        return joiningTableRepository.leftJoin(rightEntityType);
    }

    @Override
    public JoinModel rightJoin(Class<?> rightEntityType) {
        return joiningTableRepository.rightJoin(rightEntityType);
    }

    @Override
    public JoinModel innerJoin(Class<?> rightEntityType) {
        return joiningTableRepository.innerJoin(rightEntityType);
    }
}
