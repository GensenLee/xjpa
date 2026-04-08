package com.glee.xjpa.sql.where;

import com.glee.xjpa.sql.where.usermodel.XQueryWhereValues;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/21
 * @description QueryWhere
 */
public class XJpaQueryWhere implements XQueryWhere{

    private final List<IQueryWhereObject> values;

    public XJpaQueryWhere() {
        this.values = new ArrayList<>();
    }

    @Override
    public void add(IQueryWhereObject whereObject) {
        values.add(whereObject);
    }

    @Override
    public IQueryWhereObject combine() {
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
