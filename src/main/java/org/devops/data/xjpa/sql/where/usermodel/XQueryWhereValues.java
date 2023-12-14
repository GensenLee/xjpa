package org.devops.data.xjpa.sql.where.usermodel;

import org.devops.core.utils.util.ListUtil;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereNodes;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 条件组
 */
public class XQueryWhereValues implements Serializable, IQueryWhereNodes {

    private final List<IQueryWhereObject> values;
    private Condition condition;


    public XQueryWhereValues(List<IQueryWhereObject> values, Condition condition) {
        this.values = values;
        this.condition = condition;
    }


    public XQueryWhereValues(List<IQueryWhereObject> values) {
        this(values, Condition.AND);
    }

    @Override
    public Condition attachCondition() {
        return condition;
    }

    @Override
    public Map<Integer, Object> indexValues(final AtomicInteger index) {
        Map<Integer, Object> result = new HashMap<>();
        for (IQueryWhereObject value : values) {
            result.putAll(value.indexValues(index));
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return ListUtil.isNull(values) || values.stream().allMatch(IQueryWhereObject::isEmpty);
    }

    @Override
    public IQueryWhereObject condition(Condition condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public List<IQueryWhereObject> children() {
        return values;
    }

    @Override
    public boolean contains(IQueryWhereObject whereObject) {
        if (ListUtil.isNull(children())) {
            return false;
        }

        if (children().contains(whereObject)) {
            return true;
        }

        for (IQueryWhereObject child : children()) {
            if (child instanceof IQueryWhereNodes) {
                if (((IQueryWhereNodes) child).contains(whereObject)) {
                    return true;
                }
            }
        }
        return false;
    }


}
