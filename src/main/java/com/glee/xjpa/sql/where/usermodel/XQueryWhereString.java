package com.glee.xjpa.sql.where.usermodel;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import com.glee.xjpa.sql.where.objects.IQueryWhereString;
import com.glee.xjpa.sql.where.operate.Condition;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 语句条件
 */
public class XQueryWhereString implements Serializable, IQueryWhereString {

    private final String value;

    private Condition condition;

    public XQueryWhereString(String value, Condition condition) {
        this.value = value;
        this.condition = condition;
    }

    public XQueryWhereString(String value) {
        this(value, Condition.AND);
    }

    @Override
    public Condition attachCondition() {
        return condition;
    }

    @Override
    public Map<Integer, Object> indexValues(final AtomicInteger index) {
        return Collections.emptyMap();
    }

    @Override
    public boolean isEmpty() {
        return StrUtil.isEmpty(value);
    }

    @Override
    public IQueryWhereObject condition(Condition condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public String getWhereString() {
        return value;
    }
}
