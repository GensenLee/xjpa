package com.glee.xjpa.io.join;

import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.table.XJpaTableMetadata;

/**
 * @author GENSEN
 * @date 2026/1/9
 * @description
 */
@SuppressWarnings("rawtypes")
public class XJpaJoinPoint implements JoinPoint{

    private final JoiningContext joiningContext;

    private final XJpaTableMetadata XJpaTableMetadata;

    private final boolean softDeleteEnabled;

    public XJpaJoinPoint(JoiningContext joiningContext, XJpaTableMetadata XJpaTableMetadata, boolean softDeleteEnabled) {
        this.joiningContext = joiningContext;
        this.XJpaTableMetadata = XJpaTableMetadata;
        this.softDeleteEnabled = softDeleteEnabled;
        joiningContext.joinPointRegister(this);
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
        return joiningContext.getTableAlias(this);
    }

    @Override
    public TableColumn columnDef(String columnName) {
        return null;
    }

    @Override
    public boolean isSoftDeleteEnabled() {
        return softDeleteEnabled;
    }
}
