package org.devops.data.xjpa.sql.where;

import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.where.usermodel.XQueryWhereValues;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/21
 * @description QueryWhere
 */
public class XjpaQueryWhere implements XQueryWhere{

    private final List<IQueryWhereObject> values;

    public XjpaQueryWhere() {
        this.values = new ArrayList<>();
    }

    @Override
    public void add(IQueryWhereObject whereObject) {
        values.add(whereObject);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public IQueryWhereObject combine(RepositoryContext context) {
        return new XQueryWhereValues(values);
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public void clear() {
        values.clear();
    }
}
