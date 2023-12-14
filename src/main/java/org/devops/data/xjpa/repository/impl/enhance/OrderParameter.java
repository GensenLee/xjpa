package org.devops.data.xjpa.repository.impl.enhance;

import org.devops.data.xjpa.sql.executor.SortType;

class OrderParameter {
    final String column;
    final SortType sortType;

    OrderParameter(String column, SortType sortType) {
        this.column = column;
        this.sortType = sortType;
    }
}