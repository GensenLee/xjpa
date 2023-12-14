package org.devops.data.xjpa.repository;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author GENSEN
 * @date 2022/10/28
 * @description 更新
 */
public interface IUpdateRepository<K extends Serializable, V> extends IXjpaRepository<K, V> {


    default long update(String targetColumn, Object operateValue) {
        return update(targetColumn, null, operateValue, UpdateOperator.EQ);
    }

    int update(String targetColumn, String operateColumn, Object operateValue, UpdateOperator updateOperator);

    int update(UpdateRequest updateRequest);

    int update(V entity);

    int update(Collection<V> entities);

    int add(String targetColumn, Object operateValue);

    int add(String targetColumn, String operateColumn, Object operateValue);

    int subtract(String targetColumn, Object operateValue);

    int subtract(String targetColumn, String operateColumn, Object operateValue);

    int multiply(String targetColumn, Object operateValue);

    int multiply(String targetColumn, String operateColumn, Object operateValue);

    int divide(String targetColumn, Object operateValue);

    int divide(String targetColumn, String operateColumn, Object operateValue);

}
