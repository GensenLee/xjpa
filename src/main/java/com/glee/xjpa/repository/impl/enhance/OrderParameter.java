package com.glee.xjpa.repository.impl.enhance;

import com.glee.xjpa.sql.executor.SortType;

class OrderParameter {
    final String column;
    final SortType sortType;

    OrderParameter(String column, SortType sortType) {
        this.column = column;
        this.sortType = sortType;
    }
}