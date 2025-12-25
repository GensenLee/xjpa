package com.glee.xjpa.sql.where;

import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;

/**
 * @author GENSEN
 * @date 2022/11/21
 * @description where
 */
public interface XQueryWhere {

    void add(IQueryWhereObject whereObject);

    @SuppressWarnings("rawtypes")
    IQueryWhereObject combine(RepositoryContext context);

    boolean isEmpty();

    void clear();

}
