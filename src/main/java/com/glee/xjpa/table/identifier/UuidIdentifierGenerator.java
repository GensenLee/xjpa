package com.glee.xjpa.table.identifier;


import com.glee.xjpa.constant.XJpaConstant;

import java.util.UUID;

/**
 * @author GENSEN
 * @date 2021/9/16 11:09
 * @description：uuid生成器
 */
public class UuidIdentifierGenerator implements IdentifierGenerator<String> {
    @Override
    public String next() {
        return UUID.randomUUID().toString().replaceAll(XJpaConstant.CROSS_MARK, XJpaConstant.EMPTY_STRING);
    }
}
