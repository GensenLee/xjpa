package com.glee.xjpa.io;

import cn.hutool.core.bean.BeanUtil;
import com.glee.xjpa.sql.InsertSqlTemplate;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.table.TableKeyGenerator;
import com.glee.xjpa.table.TableProperties;
import com.glee.xjpa.table.XJpaTableMetadata;
import com.glee.xjpa.util.PstParameter;

import java.util.*;

/**
 * @author GENSEN
 * @date 2026/3/27
 * @description 插入请求
 */
public class InsertQueryRequest {

    protected final List<?> entityList;

    protected final TableProperties<?, ?>  tableProperties;

    protected final String insertTemplate;

    public InsertQueryRequest(List<?> entityList, TableProperties<?, ?> tableProperties) {
        this.entityList = entityList;
        this.tableProperties = tableProperties;
        this.insertTemplate = InsertSqlTemplate.buildInsertSql(tableProperties);
    }

    public String getSqlTemplate() {
        return InsertSqlTemplate.buildInsertSql(tableProperties);
    }

    public List<Map<Integer, PstParameter>> getParameters() {
        XJpaTableMetadata<?, ?> metadata = tableProperties.getMetadata();
        List<EntityTableField> tableFieldList = InsertSqlTemplate.getEntityTableFieldsForInsertion(metadata);

        List<Map<Integer, PstParameter>> result = new ArrayList<>();

        for (Object e : entityList) {
            result.add(convertObjectToPstParameters(e, tableFieldList, metadata.isAutoincrement()));
        }

        return result;
    }


    private Map<Integer, PstParameter> convertObjectToPstParameters(Object entity, List<EntityTableField> tableFieldList, boolean autoincrement) {
        Map<Integer, PstParameter> result = new HashMap<>();
        int index = 0;
        for (EntityTableField entityTableField : tableFieldList) {
            Object setVal = BeanUtil.getFieldValue(entity, entityTableField.javaField().getName());
            // 当字段是主键并且传入值为null，并且非自增时，生成一个主键
            if (setVal == null && entityTableField.isPriKey() && !autoincrement) {
                // 生成主键
                setVal = TableKeyGenerator.next(tableProperties);
            }
            PstParameter pstParameter = new PstParameter(setVal, entityTableField);
            result.put(++index, pstParameter);
        }
        return result;
    }
}