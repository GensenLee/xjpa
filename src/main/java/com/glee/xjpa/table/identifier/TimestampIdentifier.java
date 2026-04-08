package com.glee.xjpa.table.identifier;

import cn.hutool.core.date.SystemClock;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author GENSEN
 * @date 2026/3/30
 * @description 时间戳id生成器
 */
public class TimestampIdentifier implements IdentifierGenerator<Long> {

    private final AtomicLong pre = new AtomicLong(0);


    @Override
    public Long next() {
        long next = SystemClock.now();
        if (next <= pre.longValue()) {
            next  = pre.incrementAndGet();
        } else {
            pre.set(next);
        }
        return next;
    }

}
