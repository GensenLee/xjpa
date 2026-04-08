package com.glee.xjpa.sql.executor;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.exception.XJpaException;
import com.glee.xjpa.exception.XJpaExecuteException;
import com.glee.xjpa.io.QueryRequest;
import com.glee.xjpa.io.update.UpdateSqlTemplate;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.table.TableProperties;
import com.glee.xjpa.util.PreparedStatementUtil;
import com.glee.xjpa.util.PstParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2026/3/26
 * @description
 */
public class UpdateSqlExecutor<K extends Serializable, E> {

    private final static Logger log = LoggerFactory.getLogger(UpdateSqlExecutor.class);


    private final TableProperties<K, E> tableProperties;

    private final DataSourceManager dataSourceManager;

    private final SqlLogger sqlLogger;

    private final BeanDesc entityBeanDesc;

    private final EntityTableField keyTableField;

    private final PropDesc keyPropDesc;

    private final Map<String, EntityTableField> entityTableFieldMap;


    public UpdateSqlExecutor(TableProperties<K, E> tableProperties, DataSourceManager dataSourceManager, SqlLogger sqlLogger) {
        this.tableProperties = tableProperties;
        this.dataSourceManager = dataSourceManager;
        this.sqlLogger = sqlLogger;
        this.keyTableField = tableProperties.getMetadata().getPrimaryKeyField();
        this.entityBeanDesc = BeanUtil.getBeanDesc(tableProperties.getMetadata().entityType());
        this.keyPropDesc = entityBeanDesc.getProp(keyTableField.javaField().getName());
        this.entityTableFieldMap = tableProperties.getMetadata().getEntityTableFieldList()
                .stream()
                .collect(Collectors.toMap(f -> f.javaField().getName(), f -> f));
    }


    public int execute(QueryRequest queryRequest) {
        String sqlTemplate = queryRequest.getSqlTemplate();
        sqlLogger.logSql(sqlTemplate);
        Map<Integer, Object> parameters = queryRequest.getParameters();
        Connection connection = dataSourceManager.getConnection(tableProperties.getRepositoryType());
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlTemplate)) {
            parameters.forEach((index, val) -> PreparedStatementUtil.setParameter(preparedStatement, val, index));
            long start = System.currentTimeMillis();
            int affect = preparedStatement.executeUpdate();
            sqlLogger.logAffect(affect, start, System.currentTimeMillis());
            return affect;
        } catch (SQLException e) {
            log.error("sql execute fail, table = {}", tableProperties.getMetadata().getTableName(), e);
            throw new XJpaExecuteException("sql execute error", e);
        } finally {
            dataSourceManager.releaseConnection(connection, tableProperties.getRepositoryType());
        }
    }

    public int execute(Set<E> objectList) {

        // 执行批量更新前需要检查是否已经开启事务
        // 根据排序后的属性列表中的属性是否为空分组
        // 分组数组中，每一位代表一个字段状态：1非空，0为空

        Map<String, List<E>> objectGroupByFieldState = new HashMap<>();
        Map<String, List<EntityTableField>> updateFieldsGroupByFieldState = new HashMap<>();

        List<PropDesc> props = entityBeanDesc.getProps()
                .stream()
                .filter(propDesc -> !propDesc.getFieldName().equals(keyPropDesc.getFieldName()))
                .sorted(Comparator.comparing(PropDesc::getFieldName))
                .toList();

        for (E e : objectList) {
            List<EntityTableField> updateFieldList = new ArrayList<>();
            char[] state = new char[props.size()];
            for (int i = 0; i < props.size(); i++) {
                PropDesc propDesc = props.get(i);
                if (propDesc.getValue(e) == null) {
                    state[i] = '0';
                } else {
                    state[i] = '1';
                    updateFieldList.add(entityTableFieldMap.get(propDesc.getFieldName()));
                }
            }
            String key = String.valueOf(state);
            List<E> tempList = objectGroupByFieldState.computeIfAbsent(key, k -> {
                updateFieldsGroupByFieldState.put(k, updateFieldList);
                return new ArrayList<>();
            });
            tempList.add(e);
            objectGroupByFieldState.put(key, tempList);
        }

        Connection connection = dataSourceManager.getConnection(tableProperties.getRepositoryType());
        try {
            int affect = 0, index = 1;
            for (Map.Entry<String, List<E>> entry : objectGroupByFieldState.entrySet()) {
                List<E> tempObjectList = entry.getValue();
                List<EntityTableField> updateEntityFieldList = updateFieldsGroupByFieldState.get(entry.getKey());
                log.info("Starting a sub update transaction {}", index);
                int subUpdateAffect = doExecuteGroup(tempObjectList, updateEntityFieldList, connection);
                log.info("Completed sub update transaction {} with {} raw affect", index++, subUpdateAffect);
                affect += subUpdateAffect;
            }
            return affect;
        } finally {
            dataSourceManager.releaseConnection(connection, tableProperties.getRepositoryType());
        }
    }

    /**
     * 按更新字段分组执行
     *
     * @param objectList
     * @param updateFieldList
     * @return
     */
    private int doExecuteGroup(List<E> objectList, List<EntityTableField> updateFieldList, Connection connection) {


        // 构建update sql
        UpdateSqlTemplate updateSqlTemplate = new UpdateSqlTemplate(tableProperties);
        String sqlTemplate = updateSqlTemplate.toUpdateTemplate(updateFieldList);

        // 构建参数列表
        List<Map<Integer, Object>> parameters = new ArrayList<>();
        for (E e : objectList) {
            int index = 0;
            Map<Integer, Object> entityPstParameter = new HashMap<>();
            for (EntityTableField entityTableField : updateFieldList) {
                Object fieldValue = entityBeanDesc.getProp(entityTableField.javaField().getName()).getValue(e);
                entityPstParameter.put(++index, new PstParameter(fieldValue, entityTableField));
            }
            // 主键
            Object keyValue = keyPropDesc.getValue(e);
            if (keyValue == null) {
                log.warn("Skipping update for entity [{}] because primary key value is null", e.getClass().getSimpleName());
                // 无主键时跳过更新
                continue;
            }
            entityPstParameter.put(++index, keyValue);
            parameters.add(entityPstParameter);
        }

        // 更新
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlTemplate)) {
            for (Map<Integer, Object> parameter : parameters) {
                parameter.forEach((idx, val) -> PreparedStatementUtil.setParameter(preparedStatement, val, idx));
                preparedStatement.addBatch();
            }
            sqlLogger.logSql(sqlTemplate);
            long start = System.currentTimeMillis();
            int[] batch = preparedStatement.executeBatch();
            int affect = Arrays.stream(batch).sum();
            sqlLogger.logAffect(affect, start, System.currentTimeMillis());
            return affect;
        } catch (SQLException e) {
            log.error("sql execute fail, table = {}", tableProperties.getMetadata().getTableName(), e);
            throw new XJpaExecuteException("sql execute error", e);
        }
    }

    public int execute(E object) {

        Object keyValue = keyPropDesc.getValue(object);
        if (keyValue == null) {
            throw new XJpaException("Primary key value is required");
        }

        List<EntityTableField> updateFieldList = new ArrayList<>();

        Map<Integer, Object> parameters = new HashMap<>();
        int index = 0;
        for (PropDesc prop : entityBeanDesc.getProps()) {
            if (prop.getFieldName().equals(keyPropDesc.getFieldName())) {
                continue;
            }

            Object value = prop.getValue(object);
            if (value == null) {
                continue;
            }

            updateFieldList.add(entityTableFieldMap.get(prop.getFieldName()));
            parameters.put(++index, value);
        }
        parameters.put(++index, keyValue);

        UpdateSqlTemplate updateSqlTemplate = new UpdateSqlTemplate(tableProperties);
        String sqlTemplate = updateSqlTemplate.toUpdateTemplate(updateFieldList);
        Connection connection = dataSourceManager.getConnection(tableProperties.getRepositoryType());
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlTemplate)) {
            parameters.forEach((idx, val) -> PreparedStatementUtil.setParameter(preparedStatement, val, idx));
            sqlLogger.logSql(sqlTemplate);
            long start = System.currentTimeMillis();
            int affect = preparedStatement.executeUpdate();
            sqlLogger.logAffect(affect, start, System.currentTimeMillis());
            return affect;
        } catch (SQLException e) {
            log.error("sql execute fail, table = {}", tableProperties.getMetadata().getTableName(), e);
            throw new XJpaExecuteException("sql execute error", e);
        } finally {
            dataSourceManager.releaseConnection(connection, tableProperties.getRepositoryType());
        }
    }

}
