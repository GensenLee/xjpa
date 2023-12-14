package org.devops.data.xjpa.sql.where.handler;

import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/21
 * @description
 */
public interface IQueryWhereHandler {
    String toWhereString();

    boolean isEmpty();

    Map<Integer, Object> whereValues();
}
