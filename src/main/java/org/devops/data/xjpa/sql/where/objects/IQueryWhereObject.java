package org.devops.data.xjpa.sql.where.objects;

import org.devops.data.xjpa.sql.where.operate.Condition;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2022/11/22
 * @description
 */
public interface IQueryWhereObject extends IQueryWhereAttach {
    /**
     * @return 条件值 index -> value
     */
    Map<Integer, Object> indexValues(final AtomicInteger index);

    /**
     * @return
     */
    boolean isEmpty();

    /**
     * @param condition 替换条件
     * @return
     */
    @Override
    IQueryWhereObject condition(Condition condition);

    /**
     * 访问者
     * @param visitor
     */
    void accept(IQueryWhereObjectVisitor visitor);
}
