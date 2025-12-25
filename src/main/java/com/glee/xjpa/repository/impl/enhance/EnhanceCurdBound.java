package com.glee.xjpa.repository.impl.enhance;

import com.glee.xjpa.repository.IEnhanceRepository;
import com.glee.xjpa.sql.executor.LimitHandler;
import com.glee.xjpa.sql.executor.SortHandler;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author GENSEN
 * @date 2022/11/9
 * @description enhance操作绑定
 */
public interface EnhanceCurdBound<K extends Serializable, V> extends IEnhanceRepository<K, V> {

    LimitHandler getLimitHandler();

    SortHandler getSortHandler();

    Collection<String> getIncludeColumns();

    Collection<String> getDistinctColumns();

    Collection<String> getGroupingColumns();

    String getHavingString();

    boolean isIgnoreSoftDelete();

}
