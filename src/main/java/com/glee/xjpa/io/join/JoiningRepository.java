package com.glee.xjpa.io.join;

import com.glee.xjpa.io.JoiningQuery;

import javax.swing.table.TableColumn;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description 连表支持
 */
@SuppressWarnings("rawtypes")
public interface JoiningRepository {

    JoinPoint join(AbstractJoinOn joinOn);

    List<Map<String, Object>> list(JoiningQuery query);

    long count(JoiningQuery query);

    long count(TableColumn countColumn, JoiningQuery query);

    <T> List<T> list(JoiningQuery query, Class<T> resultType);
}
