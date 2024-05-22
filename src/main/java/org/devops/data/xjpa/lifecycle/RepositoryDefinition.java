package org.devops.data.xjpa.lifecycle;

import org.devops.data.xjpa.configuration.RepositoryProperties;

/**
 * @author GENSEN
 * @date 2022/11/19
 * @description 注册信息
 */
public class RepositoryDefinition {

    private final String repositoryPackageName;

    private final Class<?> repositoryType;

    private final RepositoryProperties properties;

    public RepositoryDefinition(String repositoryPackageName, Class<?> repositoryType, RepositoryProperties properties) {
        this.repositoryPackageName = repositoryPackageName;
        this.repositoryType = repositoryType;
        this.properties = properties;
    }


    public String getRepositoryPackageName() {
        return repositoryPackageName;
    }

    public Class<?> getRepositoryType() {
        return repositoryType;
    }

    public RepositoryProperties getProperties() {
        return properties;
    }

    public static RepositoryDefinitionBuilder builder() {
        return new RepositoryDefinitionBuilder();
    }


    public static final class RepositoryDefinitionBuilder {
        private String repositoryPackageName;
        private Class<?> repositoryType;
        private RepositoryProperties properties;

        private RepositoryDefinitionBuilder() {
        }

        public RepositoryDefinitionBuilder withRepositoryPackageName(String repositoryPackageName) {
            this.repositoryPackageName = repositoryPackageName;
            return this;
        }

        public RepositoryDefinitionBuilder withRepositoryType(Class<?> repositoryType) {
            this.repositoryType = repositoryType;
            return this;
        }

        public RepositoryDefinitionBuilder withProperties(RepositoryProperties properties) {
            this.properties = properties;
            return this;
        }

        public RepositoryDefinition build() {
            return new RepositoryDefinition(repositoryPackageName, repositoryType, properties);
        }
    }
}
