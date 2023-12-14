package org.devops.data.xjpa.sql.where.subquery;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2023/1/15
 * @description 内嵌子查询
 */
public interface InlineSubQuery {

    /**
     * @param explicit 是否打印条件值
     * @return 构造内嵌sql语句
     */
    String inlineSql(boolean explicit);

    /**
     * @return 条件值 index -> value
     */
    Map<Integer, Object> indexWhereValues(final AtomicInteger index);

}
