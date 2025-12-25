package com.glee.xjpa.repository.impl.enhance;

class LimitParameter {
    final Integer start;
    final Integer limit;

    LimitParameter(Integer start, Integer limit) {
        this.start = start;
        this.limit = limit;
    }
}