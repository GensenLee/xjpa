package com.glee.xjpa.sql.executor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/7
 * @description default
 */
public class DefaultSortHandler implements SortHandler{

    private final List<SortSet> sortSets;

    public DefaultSortHandler() {
        this(new ArrayList<>());
    }

    public DefaultSortHandler(List<SortSet> sortSets) {
        this.sortSets = sortSets;
    }

    @Override
    public List<SortSet> sortRequestList() {
        return sortSets;
    }

    @Override
    public boolean requiredSort() {
        return !sortSets.isEmpty();
    }

}
