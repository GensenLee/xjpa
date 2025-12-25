package com.glee.xjpa.repository;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author GENSEN
 * @date 2022/10/28
 * @description 更新
 */
public interface IUpdateRepository<K extends Serializable, V> extends IXjpaRepository<K, V> {


    default long update(String column, Object operateValue) {
        return update(column, null, operateValue, UpdateOperator.EQ);
    }

    int update(String column, String operateColumn, Object operateValue, UpdateOperator updateOperator);

    int update(UpdateRequest updateRequest);

    int update(V entity);

    int update(Collection<V> entities);

    int add(String column, Object operateValue);

    int add(String column, String operateColumn, Object operateValue);

    int subtract(String column, Object operateValue);

    int subtract(String column, String operateColumn, Object operateValue);

    int multiply(String column, Object operateValue);

    int multiply(String column, String operateColumn, Object operateValue);

    int divide(String column, Object operateValue);

    int divide(String column, String operateColumn, Object operateValue);

}
