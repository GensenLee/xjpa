package org.devops.data.xjpa.table.identifier;


import org.devops.data.xjpa.constant.XjpaConstant;

import java.util.UUID;

/**
 * @author GENSEN
 * @date 2021/9/16 11:09
 * @description：uuid生成器
 */
public class UuidIdentifierGenerator implements IdentifierGenerator<String> {
    @Override
    public String next() {
        return UUID.randomUUID().toString().replaceAll(XjpaConstant.CROSS_MARK, XjpaConstant.EMPTY_STRING);
    }
}
