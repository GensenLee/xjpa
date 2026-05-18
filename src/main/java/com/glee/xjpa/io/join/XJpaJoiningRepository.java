package com.glee.xjpa.io.join;

import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.exception.XJpaException;
import com.glee.xjpa.io.JoiningGroupByQuery;
import com.glee.xjpa.io.JoiningQuery;
import com.glee.xjpa.io.StandardXJpaRepository;
import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.sql.JoiningSqlTemplate;
import com.glee.xjpa.sql.executor.JoiningSqlExecutor;
import com.glee.xjpa.sql.logger.SqlLogger;

import java.util.*;

/**
 * @author GENSEN
 * @date 2026/1/8
 * @description
 */
@SuppressWarnings("rawtypes")
public class XJpaJoiningRepository implements JoiningRepository, JoiningContext {

    private final Map<JoinPoint, JoiningTableIndex> joiningTableIndexMap;

    private final InitJoinPoint initJoinPoint;

    private final List<JoinPoint> joinPointOrder;

    private final Map<JoinPoint, AbstractJoinOn> joinOnMap;

    private final DataSourceManager dataSourceManager;

    private final SqlLogger sqlLogger;

    private final Class<?> repositoryClass;

    public XJpaJoiningRepository(InitJoinPoint initJoinPoint) {
        this.initJoinPoint = initJoinPoint;
        this.dataSourceManager = null;
        this.sqlLogger = null;
        this.repositoryClass = null;
        this.joiningTableIndexMap = new HashMap<>();
        this.joinPointOrder = new ArrayList<>();
        this.joinOnMap = new HashMap<>();
        joinPointRegister(initJoinPoint);
        joinPointOrder.add(initJoinPoint);
    }

    public XJpaJoiningRepository(InitJoinPoint initJoinPoint, DataSourceManager dataSourceManager, SqlLogger sqlLogger) {
        this.initJoinPoint = initJoinPoint;
        this.dataSourceManager = dataSourceManager;
        this.sqlLogger = sqlLogger;
        this.repositoryClass = initJoinPoint.getDrivenRepository().getClass();
        this.joiningTableIndexMap = new HashMap<>();
        this.joinPointOrder = new ArrayList<>();
        this.joinOnMap = new HashMap<>();
        joinPointRegister(initJoinPoint);
        joinPointOrder.add(initJoinPoint);
    }

    @Override
    public JoinPoint join(AbstractJoinOn joinOn) {
        XJpaJoinPoint joinPoint = new XJpaJoinPoint(this, joinOn.getJoinEntity(), joinOn.isSoftDeleteEnabled());
        joinOnMap.put(joinPoint, joinOn);
        joinPointOrder.add(joinPoint);
        return joinPoint;
    }

    @Override
    public List<Map<String, Object>> list(JoiningQuery query) {
        String sql = JoiningSqlTemplate.buildJoinSql(query, JoiningSqlTemplate.buildFromClause(this), JoiningSqlTemplate.buildJoinClause(this));
        JoiningSqlExecutor executor = new JoiningSqlExecutor(dataSourceManager, repositoryClass, sqlLogger);
        return executor.executeQuery(sql);
    }

    @Override
    public long count(JoiningQuery query) {
        String sql = JoiningSqlTemplate.buildCountSql(query, JoiningSqlTemplate.buildFromClause(this), JoiningSqlTemplate.buildJoinClause(this));
        JoiningSqlExecutor executor = new JoiningSqlExecutor(dataSourceManager, repositoryClass, sqlLogger);
        return executor.executeCount(sql);
    }

    @Override
    public long count(TableColumn countColumn, JoiningQuery query) {
        String countColumnLabel = countColumn != null ? countColumn.getColumnLabel() : null;
        String sql = JoiningSqlTemplate.buildCountSql(query, JoiningSqlTemplate.buildFromClause(this), JoiningSqlTemplate.buildJoinClause(this), countColumnLabel);
        JoiningSqlExecutor executor = new JoiningSqlExecutor(dataSourceManager, repositoryClass, sqlLogger);
        return executor.executeCount(sql);
    }

    @Override
    public <T> List<T> list(JoiningQuery query, Class<T> resultType) {
        String sql = JoiningSqlTemplate.buildJoinSql(query, JoiningSqlTemplate.buildFromClause(this), JoiningSqlTemplate.buildJoinClause(this));
        JoiningSqlExecutor executor = new JoiningSqlExecutor(dataSourceManager, repositoryClass, sqlLogger);
        List<Map<String, Object>> result = executor.executeQuery(sql);
        return convertToEntityList(result, resultType);
    }

    public List<Map<String, Object>> list(JoiningGroupByQuery query) {
        String sql = JoiningSqlTemplate.buildJoinSql(query, JoiningSqlTemplate.buildFromClause(this), JoiningSqlTemplate.buildJoinClause(this));
        JoiningSqlExecutor executor = new JoiningSqlExecutor(dataSourceManager, repositoryClass, sqlLogger);
        return executor.executeQuery(sql);
    }

    public long count(JoiningGroupByQuery query) {
        String sql = JoiningSqlTemplate.buildCountSql(query, JoiningSqlTemplate.buildFromClause(this), JoiningSqlTemplate.buildJoinClause(this));
        JoiningSqlExecutor executor = new JoiningSqlExecutor(dataSourceManager, repositoryClass, sqlLogger);
        return executor.executeCount(sql);
    }

    public <T> List<T> list(JoiningGroupByQuery query, Class<T> resultType) {
        String sql = JoiningSqlTemplate.buildJoinSql(query, JoiningSqlTemplate.buildFromClause(this), JoiningSqlTemplate.buildJoinClause(this));
        JoiningSqlExecutor executor = new JoiningSqlExecutor(dataSourceManager, repositoryClass, sqlLogger);
        List<Map<String, Object>> result = executor.executeQuery(sql);
        return convertToEntityList(result, resultType);
    }

    private <T> List<T> convertToEntityList(List<Map<String, Object>> result, Class<T> resultType) {
        List<T> list = new ArrayList<>();
        try {
            for (Map<String, Object> row : result) {
                T instance = resultType.getDeclaredConstructor().newInstance();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    try {
                        java.lang.reflect.Field field = resultType.getDeclaredField(entry.getKey());
                        field.setAccessible(true);
                        field.set(instance, entry.getValue());
                    } catch (NoSuchFieldException e) {
                        // ignore unknown fields
                    }
                }
                list.add(instance);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert result to " + resultType.getName(), e);
        }
        return list;
    }

    @Override
    public void joinPointRegister(JoinPoint joinPoint) {
        int index = joiningTableIndexMap.size() + 1;
        JoiningTableIndex joiningTableIndex = new JoiningTableIndex(index, createTableAlias(joinPoint, index), joinPoint.isSoftDeleteEnabled());
        joiningTableIndexMap.put(joinPoint, joiningTableIndex);
    }

    private String createTableAlias(JoinPoint joinPoint, int index) {
        return "t%d".formatted(index);
    }

    @Override
    public String getTableAlias(JoinPoint joinPoint) {
        return Optional.ofNullable(joiningTableIndexMap.get(joinPoint))
                .map(JoiningTableIndex::tableAlias)
                .orElseThrow(() -> new XJpaException("join point does not exist"));
    }

    @Override
    public int getJoiningOrder(JoinPoint joinPoint) {
        return Optional.ofNullable(joiningTableIndexMap.get(joinPoint))
                .map(JoiningTableIndex::index)
                .orElseThrow(() -> new XJpaException("join point does not exist"));
    }

    public InitJoinPoint getInitJoinPoint() {
        return initJoinPoint;
    }

    public List<JoinPoint> getJoinPointOrder() {
        return joinPointOrder;
    }

    public Map<JoinPoint, AbstractJoinOn> getJoinOnMap() {
        return joinOnMap;
    }

}