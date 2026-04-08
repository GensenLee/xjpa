package com.glee.xjpa.io.join;

import com.glee.xjpa.io.StandardXJpaRepository;
import com.glee.xjpa.io.column.TableColumn;


/**
 * @author GENSEN
 * @date 2026/1/8
 * @description 初始连接点
 */
@SuppressWarnings("rawtypes")
public class InitJoinPoint implements JoinPoint {

    /**
     * 驱动表
     */
    private final StandardXJpaRepository drivenRepository;

    private final boolean softDeleteEnabled;

    public InitJoinPoint(StandardXJpaRepository drivenRepository) {
        this.drivenRepository = drivenRepository;
        this.softDeleteEnabled = false;
    }

    public InitJoinPoint(StandardXJpaRepository drivenRepository, boolean softDeleteEnabled) {
        this.drivenRepository = drivenRepository;
        this.softDeleteEnabled = softDeleteEnabled;
    }

    @Override
    public Class getEntityType() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public String getTableAlias() {
        return null;
    }

    @Override
    public TableColumn columnDef(String columnName) {
        return null;
    }

    StandardXJpaRepository getDrivenRepository() {
        return drivenRepository;
    }

    @Override
    public boolean isSoftDeleteEnabled() {
        return softDeleteEnabled;
    }
}
