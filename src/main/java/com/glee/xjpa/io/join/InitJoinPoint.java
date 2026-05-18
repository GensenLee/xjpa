package com.glee.xjpa.io.join;

import com.glee.xjpa.io.StandardXJpaRepository;
import com.glee.xjpa.io.column.JoinPointTableColumn;
import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.table.XJpaTableMetadata;
import com.glee.xjpa.util.TableUtil;

import java.lang.reflect.Type;


/**
 * @author GENSEN
 * @date 2026/1/8
 * @description 初始连接点
 */
@SuppressWarnings("rawtypes")
public class InitJoinPoint implements JoinPoint {

    private final StandardXJpaRepository drivenRepository;

    private final XJpaTableMetadata metadata;

    private final boolean softDeleteEnabled;

    public InitJoinPoint(StandardXJpaRepository drivenRepository) {
        this.drivenRepository = drivenRepository;
        this.metadata = null;
        this.softDeleteEnabled = false;
    }

    public InitJoinPoint(StandardXJpaRepository drivenRepository, boolean softDeleteEnabled) {
        this.drivenRepository = drivenRepository;
        this.metadata = null;
        this.softDeleteEnabled = softDeleteEnabled;
    }

    InitJoinPoint(StandardXJpaRepository drivenRepository, XJpaTableMetadata metadata, boolean softDeleteEnabled) {
        this.drivenRepository = drivenRepository;
        this.metadata = metadata;
        this.softDeleteEnabled = softDeleteEnabled;
    }

    @Override
    public Class getEntityType() {
        Type type = TableUtil.getTableEntityType(drivenRepository.getClass());
        if (type instanceof Class) {
            return (Class) type;
        }
        throw new IllegalStateException("Entity type is not a Class: " + type);
    }

    @Override
    public String getTableName() {
        return TableUtil.getTableNameByEntityType(getEntityType());
    }

    @Override
    public String getTableAlias() {
        return "t1";
    }

    @Override
    public TableColumn columnDef(String columnName) {
        return new JoinPointTableColumn(this, columnName);
    }

    @Override
    public boolean isSoftDeleteEnabled() {
        return softDeleteEnabled;
    }

    @Override
    public int getJoiningOrder() {
        return 1;
    }

    StandardXJpaRepository getDrivenRepository() {
        return drivenRepository;
    }

    XJpaTableMetadata getMetadata() {
        return metadata;
    }
}