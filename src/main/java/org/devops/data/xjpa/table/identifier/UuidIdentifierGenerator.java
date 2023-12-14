package org.devops.data.xjpa.table.identifier;

import org.devops.core.utils.constant.CommonConstant;

import java.util.UUID;

/**
 * @author GENSEN
 * @date 2021/9/16 11:09
 * @description：uuid生成器
 */
public class UuidIdentifierGenerator implements IdentifierGenerator<String> {
    @Override
    public String next() {
        return UUID.randomUUID().toString().replaceAll(CommonConstant.CROSS_MARK, CommonConstant.EMPTY_STRING);
    }
}
