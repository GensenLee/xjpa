package org.devops.data.xjpa.sql.where.handler;

import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2022/9/6
 * @description 条件语句构造
 */
public abstract class AbstractQueryWhereHandler implements IQueryWhereHandler {

    protected IQueryWhereObject whereValue;

    public AbstractQueryWhereHandler(IQueryWhereObject whereValue) {
        this.whereValue = whereValue;
    }

    @Override
    public boolean isEmpty() {
        return whereValue == null || whereValue.isEmpty();
    }

    /**
     * @return
     */
    @Override
    public Map<Integer, Object> whereValues() {
        // PrepareStatement index 从1开始
        if (whereValue == null) {
            return Collections.emptyMap();
        } else {
            final AtomicInteger index = new AtomicInteger(1);
            return whereValue.indexValues(index);
        }
    }

}
