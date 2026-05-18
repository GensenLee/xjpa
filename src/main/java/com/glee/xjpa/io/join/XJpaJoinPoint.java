package com.glee.xjpa.io.join;

import com.glee.xjpa.io.column.JoinPointTableColumn;
import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.table.XJpaTableMetadata;
import com.glee.xjpa.util.TableUtil;


/**
 * @author GENSEN
 * @date 2026/1/9
 * @description
 */
@SuppressWarnings("rawtypes")
public class XJpaJoinPoint implements JoinPoint {

    private final JoiningContext joiningContext;

    private final XJpaTableMetadata xJpaTableMetadata;

    private final boolean softDeleteEnabled;

    private final Class entityType;

    private final String tableName;

    public XJpaJoinPoint(JoiningContext joiningContext, XJpaTableMetadata xJpaTableMetadata, boolean softDeleteEnabled) {
        this.joiningContext = joiningContext;
        this.xJpaTableMetadata = xJpaTableMetadata;
        this.softDeleteEnabled = softDeleteEnabled;
        this.entityType = xJpaTableMetadata.entityType();
        this.tableName = TableUtil.getTableNameByEntityType(entityType);
        joiningContext.joinPointRegister(this);
    }

    public XJpaJoinPoint(JoiningContext joiningContext, Class entityType, boolean softDeleteEnabled) {
        this.joiningContext = joiningContext;
        this.xJpaTableMetadata = null;
        this.softDeleteEnabled = softDeleteEnabled;
        this.entityType = entityType;
        this.tableName = TableUtil.getTableNameByEntityType(entityType);
        joiningContext.joinPointRegister(this);
    }

    @Override
    public Class getEntityType() {
        return entityType;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getTableAlias() {
        return joiningContext.getTableAlias(this);
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
        return joiningContext.getJoiningOrder(this);
    }

    XJpaTableMetadata getXJpaTableMetadata() {
        return xJpaTableMetadata;
    }
}