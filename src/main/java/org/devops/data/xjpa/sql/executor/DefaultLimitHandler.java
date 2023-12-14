package org.devops.data.xjpa.sql.executor;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description default
 */
public class DefaultLimitHandler implements LimitHandler{

    private final int start;

    private final int limit;

    public DefaultLimitHandler(int start, int limit) {
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
        return limit > 0;
    }
}
