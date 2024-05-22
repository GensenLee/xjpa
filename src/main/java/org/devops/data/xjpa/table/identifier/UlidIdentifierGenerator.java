package org.devops.data.xjpa.table.identifier;


/**
 * @author GENSEN
 * @date 2021/9/16 11:09
 * @description：ulid生成器 https://github.com/ulid/spec
 */
public class UlidIdentifierGenerator implements IdentifierGenerator<String> {

    private final ULID ulid;

    public UlidIdentifierGenerator() {
        ulid = new ULID();
    }

    @Override
    public String next() {
        return ulid.nextULID();
    }
}
