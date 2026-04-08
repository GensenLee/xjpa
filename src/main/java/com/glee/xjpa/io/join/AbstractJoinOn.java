package com.glee.xjpa.io.join;

import cn.hutool.core.lang.Pair;
import jakarta.persistence.criteria.JoinType;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description
 */
@SuppressWarnings("rawtypes")
public class AbstractJoinOn implements JoinOn {

    private final Class rightTableEntityType;

    private final JoinType joinType;

    private final Set<Pair<String, String>> joiningOnColumns;

    private final boolean softDeleteEnabled;

    public AbstractJoinOn(Class rightTableEntityType, JoinType joinType) {
        this.rightTableEntityType = rightTableEntityType;
        this.joinType = joinType;
        this.softDeleteEnabled = false;
        this.joiningOnColumns = new HashSet<>();
    }

    public AbstractJoinOn(Class rightTableEntityType, JoinType joinType, boolean softDeleteEnabled) {
        this.rightTableEntityType = rightTableEntityType;
        this.joinType = joinType;
        this.softDeleteEnabled = softDeleteEnabled;
        this.joiningOnColumns = new HashSet<>();
    }

    Class getJoinEntity() {
        return rightTableEntityType;
    }

    JoinType getJoinType() {
        return joinType;
    }

    /**
     * @return 连接字段 <当前表字段名，被连接表字段>
     */
    Set<Pair<String, String>> getJoiningOnColumns() {
        return joiningOnColumns;
    }

    boolean isEmpty() {
        return joiningOnColumns.isEmpty();
    }

    boolean isSoftDeleteEnabled() {
        return softDeleteEnabled;
    }

    @Override
    public AbstractJoinOn on(String columnNameOnJoiningTable, String columnNameOnConnectedTable) {
        Assert.hasLength(columnNameOnJoiningTable, "invalid columnNameOnJoiningTable");
        Assert.hasLength(columnNameOnConnectedTable, "invalid columnNameOnConnectedTable");
        joiningOnColumns.add(Pair.of(columnNameOnJoiningTable, columnNameOnConnectedTable));
        return this;
    }

}
