package com.glee.xjpa.table.identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2021/9/15 17:00
 * @description：id工厂
 */
public class DefaultIdentifierGeneratorFactory implements IdentifierGeneratorFactory {

    private final Map<String, IdentifierGenerator<?>> table_generator_cache = new HashMap<>();

    @Override
    public IdentifierGenerator<?> getGenerator(IdentifierGeneratorType identifierGeneratorType, String key) {
        IdentifierGenerator<?> instance = table_generator_cache.get(key);
        if (instance != null) {
            return instance;
        }
        instance = identifierGeneratorType.createIdentifierGenerator(key);
        table_generator_cache.put(key, instance);
        return instance;
    }
}
