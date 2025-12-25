package com.glee.xjpa.table.identifier;


import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.RandomUtil;

import java.math.BigInteger;

/**
 * @author GENSEN
 * @date 2021/9/15 16:21
 * @description：雪花id
 */
public class SnowflakeIdentifierGenerator implements IdentifierGenerator<String> {

    /**
     * 将生成值装成radix位的字符串
     */
    private final int radix;

    /**
     * 机器 ID 部分(影响雪花ID)
     */
    private final Long workerId;

    /**
     * 数据标识 ID 部分(影响雪花ID)(workerId 和 datacenterId 一起配置才能重新初始化 Sequence)
     * <p>
     * 每张表对应一个datacenterId
     */
    private final Long datacenterId;


    private final SnowflakeIdGenerator snowflakeIdGenerator;


    public SnowflakeIdentifierGenerator(String key, int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new IllegalArgumentException("radix 超出限制");
        }
        this.radix = radix;

        String localIP = NetUtil.getLocalhostStr();
        this.workerId = Ipv4Util.ipv4ToLong(localIP) % 31;
        BigInteger random = new BigInteger(String.valueOf(getNameId(key) + RandomUtil.randomLong(4)));
        this.datacenterId = Math.abs(random.mod(BigInteger.valueOf(31)).longValue());
        this.snowflakeIdGenerator = new SnowflakeIdGenerator(datacenterId, workerId);
    }

    private SnowflakeIdGenerator getSnowflakeIdGenerator() {
        return snowflakeIdGenerator;
    }

    @Override
    public String next() {
        long nextId = getSnowflakeIdGenerator().nextId();
        return Long.toUnsignedString(nextId, this.radix);
    }

    /**
     * 使用ascll码生成数字
     *
     * @param name
     * @return
     */
    private static long getNameId(String name) {
        int maxLength = String.valueOf(Long.MAX_VALUE).length();
        StringBuilder tmp = new StringBuilder();
        for (char c : name.toCharArray()) {
            tmp.append((int) c);
            if (tmp.length() >= maxLength) {
                tmp = new StringBuilder(tmp.substring(tmp.length() - maxLength, maxLength));
            }
        }
        if (tmp.toString().compareTo(String.valueOf(Long.MAX_VALUE)) <= 0) {
            return Long.parseLong(tmp.toString());
        }
        BigInteger bigInteger = new BigInteger(tmp.toString());
        return bigInteger.mod(new BigInteger(String.valueOf(Long.MAX_VALUE))).longValue();
    }

}
