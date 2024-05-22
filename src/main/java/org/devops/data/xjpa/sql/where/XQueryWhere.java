package org.devops.data.xjpa.sql.where;

import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;

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
