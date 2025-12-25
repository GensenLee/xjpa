package com.glee.xjpa.table.identifier;

import java.util.Arrays;

/**
 * @author GENSEN
 * @date 2021/9/15 16:40
 * @description：id生成方式
 */
public enum IdentifierGeneratorType {

    /**
     * 空，不自动生成键
     */
    None{
        @Override
        public IdentifierGenerator<?> createIdentifierGenerator(String key) {
            return null;
        }
    },

    /**
     * 雪花id
     * 仅支持number类型字段
     */
    Snowflake{
        @Override
        public IdentifierGenerator<?> createIdentifierGenerator(String key) {
            return new SnowflakeIdentifierGenerator(key, 10);
        }
    },
    /**
     * 雪花id的16位形式
     */
    SnowflakeTo16{
        @Override
        public IdentifierGenerator<?> createIdentifierGenerator(String key) {
            return new SnowflakeIdentifierGenerator(key, 16);
        }
    },
    /**
     * 雪花id的32位形式
     */
    SnowflakeTo32{
        @Override
        public IdentifierGenerator<?> createIdentifierGenerator(String key) {
            return new SnowflakeIdentifierGenerator(key, 32);
        }
    },
    /**
     * 雪花id的36位形式
     */
    SnowflakeTo36{
        @Override
        public IdentifierGenerator<?> createIdentifierGenerator(String key) {
            return new SnowflakeIdentifierGenerator(key, 36);
        }
    },
    /**
     * <a href="https://github.com/ulid/spec">...</a>
     * 仅支持string类型字段
     */
    ULID{
        @Override
        public IdentifierGenerator<?> createIdentifierGenerator(String key) {
            return new UlidIdentifierGenerator();
        }
    },
    /**
     * 仅支持string类型字段
     */
    UUID{
        @Override
        public IdentifierGenerator<?> createIdentifierGenerator(String key) {
            return new UuidIdentifierGenerator();
        }
    };

    public abstract IdentifierGenerator<?> createIdentifierGenerator(String key);

    public static IdentifierGeneratorType getInstance(String name) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}
