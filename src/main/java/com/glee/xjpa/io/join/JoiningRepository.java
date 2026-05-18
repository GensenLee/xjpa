package com.glee.xjpa.io.join;

import com.glee.xjpa.io.JoiningQuery;
import com.glee.xjpa.io.column.TableColumn;

import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description 连表支持
 */
@SuppressWarnings("rawtypes")
public interface JoiningRepository {

    /**
     * 使用 JoinSpec 指定完整的连接参数
     * @param joinSpec 连接规格说明
     * @return JoinPoint 连接点，可以用于后续的 where 条件中指定字段来源
     */
    JoinPoint join(JoinSpec joinSpec);

    /**
     * 简化版 join 方法 - 左连接
     * @param rightTableEntityType 右表实体类型
     * @param sourceColumn 源表字段（当前表的字段）
     * @param targetColumn 目标表字段（被连接表的字段）
     * @return JoinPoint 连接点
     */
    JoinPoint leftJoin(Class rightTableEntityType, String sourceColumn, String targetColumn);

    /**
     * 简化版 join 方法 - 右连接
     * @param rightTableEntityType 右表实体类型
     * @param sourceColumn 源表字段（当前表的字段）
     * @param targetColumn 目标表字段（被连接表的字段）
     * @return JoinPoint 连接点
     */
    JoinPoint rightJoin(Class rightTableEntityType, String sourceColumn, String targetColumn);

    /**
     * 简化版 join 方法 - 内连接
     * @param rightTableEntityType 右表实体类型
     * @param sourceColumn 源表字段（当前表的字段）
     * @param targetColumn 目标表字段（被连接表的字段）
     * @return JoinPoint 连接点
     */
    JoinPoint innerJoin(Class rightTableEntityType, String sourceColumn, String targetColumn);

    List<Map<String, Object>> list(JoiningQuery query);

    long count(JoiningQuery query);

    long count(TableColumn countColumn, JoiningQuery query);

    <T> List<T> list(JoiningQuery query, Class<T> resultType);
}