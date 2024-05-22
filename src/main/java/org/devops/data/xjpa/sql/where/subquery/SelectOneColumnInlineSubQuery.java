package org.devops.data.xjpa.sql.where.subquery;

import org.devops.data.xjpa.sql.where.QueryWhereUtil;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.util.TableUtil;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2023/1/15
 * @description 单列查询内嵌
 */
@SuppressWarnings("rawtypes")
public class SelectOneColumnInlineSubQuery implements InlineSubQuery{

    /**
     * 查询表
     */
    private final Class entityType;

    /**
     * 查询列
     */
    private final String selectColumn;

    /**
     * 查询条件
     */
    private final IQueryWhereObject whereObject;

    SelectOneColumnInlineSubQuery(Class entityType, String selectColumn, IQueryWhereObject whereObject) {
        this.entityType = entityType;
        this.selectColumn = selectColumn;
        this.whereObject = whereObject;
        Assert.notNull(entityType, "invalid entityType");
        Assert.notNull(selectColumn, "invalid selectColumn");
    }

    SelectOneColumnInlineSubQuery(Class entityType, String selectColumn) {
        this(entityType, selectColumn, null);
    }

    @Override
    public String inlineSql(boolean explicit) {

        StringBuilder sqlBuilder = new StringBuilder(" (select `")
                .append(selectColumn)
                .append("` from `")
                .append(TableUtil.getTableNameByEntityType(entityType))
                .append("`");

        if (whereObject != null) {
            sqlBuilder.append(" where ")
                    .append(QueryWhereUtil.toWhereString(whereObject, explicit));
        }
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }

    @Override
    public Map<Integer, Object> indexWhereValues(AtomicInteger index) {
        if (whereObject == null) {
            return Collections.emptyMap();
        }

        return whereObject.indexValues(index);
    }
}
