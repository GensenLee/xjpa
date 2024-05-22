package org.devops.data.xjpa.sql.where.subquery;

import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;

/**
 * @author GENSEN
 * @date 2023/1/15
 * @description 子查询
 */
@SuppressWarnings("rawtypes")
public class SubQuery {

    /**
     * 单列查询
     * @param entityType
     * @param column
     * @param whereObject
     * @return
     */
    public static InlineSubQuery selectOneColumn(Class entityType, String column, IQueryWhereObject whereObject){
        return new SelectOneColumnInlineSubQuery(entityType, column, whereObject);
    }

    /**
     * @param entityType
     * @param column
     * @return
     */
    public static InlineSubQuery selectOneColumn(Class entityType, String column){
        return new SelectOneColumnInlineSubQuery(entityType, column);
    }

}
