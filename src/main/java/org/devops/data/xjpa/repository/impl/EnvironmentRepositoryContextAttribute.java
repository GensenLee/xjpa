package org.devops.data.xjpa.repository.impl;

import org.springframework.core.env.Environment;

/**
 * @author GENSEN
 * @date 2022/11/10
 * @description 支持环境变量
 */
public class EnvironmentRepositoryContextAttribute extends ThreadLocalRepositoryContextAttribute {

    private final Environment environment;

    public EnvironmentRepositoryContextAttribute(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object getAttribute(String key) {
        Object attribute = super.getAttribute(key);
        if (attribute != null) {
            return attribute;
        }
        return environment.getProperty(key);
    }
}
