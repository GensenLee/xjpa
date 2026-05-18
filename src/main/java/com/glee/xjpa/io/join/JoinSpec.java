package com.glee.xjpa.io.join;

import cn.hutool.core.lang.Pair;
import jakarta.persistence.criteria.JoinType;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接规格说明 - 用于定义一次 JOIN 操作的完整参数
 */
@SuppressWarnings("rawtypes")
public class JoinSpec {

    private final Class rightTableEntityType;

    private final JoinType joinType;

    private final Set<Pair<String, String>> joiningOnColumns;

    private final boolean softDeleteEnabled;

    /**
     * 构造函数
     */
    JoinSpec(Class rightTableEntityType, JoinType joinType, Set<Pair<String, String>> joiningOnColumns, boolean softDeleteEnabled) {
        this.rightTableEntityType = rightTableEntityType;
        this.joinType = joinType;
        this.joiningOnColumns = joiningOnColumns;
        this.softDeleteEnabled = softDeleteEnabled;
    }

    /**
     * 创建左连接规格
     * @param rightTableEntityType 右表实体类型
     * @param sourceColumn 源表字段（当前表的字段）
     * @param targetColumn 目标表字段（被连接表的字段）
     * @return JoinSpec
     */
    public static JoinSpec leftJoin(Class rightTableEntityType, String sourceColumn, String targetColumn) {
        Assert.notNull(rightTableEntityType, "rightTableEntityType cannot be null");
        Assert.hasLength(sourceColumn, "sourceColumn cannot be empty");
        Assert.hasLength(targetColumn, "targetColumn cannot be empty");
        
        Set<Pair<String, String>> columns = new HashSet<>();
        columns.add(Pair.of(sourceColumn, targetColumn));
        return new JoinSpec(rightTableEntityType, JoinType.LEFT, columns, false);
    }

    /**
     * 创建左连接规格（带多个on条件）
     * @param rightTableEntityType 右表实体类型
     * @param onColumns on条件列表，每个Pair为(sourceColumn, targetColumn)
     * @return JoinSpec
     */
    public static JoinSpec leftJoin(Class rightTableEntityType, Set<Pair<String, String>> onColumns) {
        Assert.notNull(rightTableEntityType, "rightTableEntityType cannot be null");
        Assert.notEmpty(onColumns, "onColumns cannot be empty");
        
        return new JoinSpec(rightTableEntityType, JoinType.LEFT, onColumns, false);
    }

    /**
     * 创建右连接规格
     * @param rightTableEntityType 右表实体类型
     * @param sourceColumn 源表字段（当前表的字段）
     * @param targetColumn 目标表字段（被连接表的字段）
     * @return JoinSpec
     */
    public static JoinSpec rightJoin(Class rightTableEntityType, String sourceColumn, String targetColumn) {
        Assert.notNull(rightTableEntityType, "rightTableEntityType cannot be null");
        Assert.hasLength(sourceColumn, "sourceColumn cannot be empty");
        Assert.hasLength(targetColumn, "targetColumn cannot be empty");
        
        Set<Pair<String, String>> columns = new HashSet<>();
        columns.add(Pair.of(sourceColumn, targetColumn));
        return new JoinSpec(rightTableEntityType, JoinType.RIGHT, columns, false);
    }

    /**
     * 创建右连接规格（带多个on条件）
     * @param rightTableEntityType 右表实体类型
     * @param onColumns on条件列表，每个Pair为(sourceColumn, targetColumn)
     * @return JoinSpec
     */
    public static JoinSpec rightJoin(Class rightTableEntityType, Set<Pair<String, String>> onColumns) {
        Assert.notNull(rightTableEntityType, "rightTableEntityType cannot be null");
        Assert.notEmpty(onColumns, "onColumns cannot be empty");
        
        return new JoinSpec(rightTableEntityType, JoinType.RIGHT, onColumns, false);
    }

    /**
     * 创建内连接规格
     * @param rightTableEntityType 右表实体类型
     * @param sourceColumn 源表字段（当前表的字段）
     * @param targetColumn 目标表字段（被连接表的字段）
     * @return JoinSpec
     */
    public static JoinSpec innerJoin(Class rightTableEntityType, String sourceColumn, String targetColumn) {
        Assert.notNull(rightTableEntityType, "rightTableEntityType cannot be null");
        Assert.hasLength(sourceColumn, "sourceColumn cannot be empty");
        Assert.hasLength(targetColumn, "targetColumn cannot be empty");
        
        Set<Pair<String, String>> columns = new HashSet<>();
        columns.add(Pair.of(sourceColumn, targetColumn));
        return new JoinSpec(rightTableEntityType, JoinType.INNER, columns, false);
    }

    /**
     * 创建内连接规格（带多个on条件）
     * @param rightTableEntityType 右表实体类型
     * @param onColumns on条件列表，每个Pair为(sourceColumn, targetColumn)
     * @return JoinSpec
     */
    public static JoinSpec innerJoin(Class rightTableEntityType, Set<Pair<String, String>> onColumns) {
        Assert.notNull(rightTableEntityType, "rightTableEntityType cannot be null");
        Assert.notEmpty(onColumns, "onColumns cannot be empty");
        
        return new JoinSpec(rightTableEntityType, JoinType.INNER, onColumns, false);
    }

    /**
     * 设置软删除启用
     * @return JoinSpec
     */
    public JoinSpec withSoftDelete() {
        return new JoinSpec(this.rightTableEntityType, this.joinType, this.joiningOnColumns, true);
    }

    // Getters
    public Class getRightTableEntityType() {
        return rightTableEntityType;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public Set<Pair<String, String>> getJoiningOnColumns() {
        return joiningOnColumns;
    }

    public boolean isSoftDeleteEnabled() {
        return softDeleteEnabled;
    }
}