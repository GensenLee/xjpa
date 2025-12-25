package com.glee.xjpa.sql.executor;


import org.springframework.util.Assert;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 分页处理
 */
public interface LimitHandler {

    int getStart();

    int getLimit();

    boolean requireLimit();

    static LimitHandler empty() {
        return new Limit(-1, -1);
    }

    static LimitHandler limit(int start, int limit) {
        Assert.isTrue(start >= 0, "start must >= 0");
        return new Limit(start, limit);
    }

    class Limit implements LimitHandler {

        private final int start;
        private final int limit;

        public Limit(int start, int limit) {
            this.start = start;
            this.limit = limit;
        }

        @Override
        public int getStart() {
            return start;
        }

        @Override
        public int getLimit() {
            return limit;
        }

        @Override
        public boolean requireLimit() {
            return start >= 0;
        }
    }
}
