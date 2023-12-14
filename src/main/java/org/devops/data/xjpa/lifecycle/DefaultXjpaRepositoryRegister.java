package org.devops.data.xjpa.lifecycle;

import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.devops.data.xjpa.repository.impl.RepositoryProxyBeanFactoryFactory;
import org.devops.data.xjpa.repository.impl.proxy.JdkDelegateModeXjpaRepositoryBeanProxy;
import org.devops.data.xjpa.repository.impl.proxy.XjpaRepositoryBeanProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.core.env.Environment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author GENSEN
 * @date 2022/11/19
 * @description 管理注册
 */
@SuppressWarnings("rawtypes")
public class DefaultXjpaRepositoryRegister implements XjpaRepositoryRegister {
    protected static final Logger logger = LoggerFactory.getLogger(DefaultXjpaRepositoryRegister.class);

    /**
     * 已经注册的bean, type key
     */
    protected final Map<Class, StandardJpaRepository> typeRegisteredRepositories;

    /**
     * 已经注册的bean, name key
     */
    protected final Map<String, RepositoryDefinition> nameRegisteredRepositories;

    private final Environment environment;

    private final DefaultListableBeanFactory beanFactory;

    private final BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    private final RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory;

    private final XjpaRepositoryBeanProxy xjpaRepositoryBeanProxy;

    public DefaultXjpaRepositoryRegister(Environment environment, DefaultListableBeanFactory beanFactory,
                                         RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory) {
        this.environment = environment;
        this.beanFactory = beanFactory;
        this.implProxyBeanFactoryFactory = implProxyBeanFactoryFactory;
        this.xjpaRepositoryBeanProxy = new JdkDelegateModeXjpaRepositoryBeanProxy(implProxyBeanFactoryFactory);
        this.typeRegisteredRepositories = new HashMap<>();
        this.nameRegisteredRepositories = new HashMap<>();
    }

    @Override
    public String register(RepositoryDefinition repositoryDefinition) {

        StandardJpaRepository repositoryInstance = (StandardJpaRepository)
                xjpaRepositoryBeanProxy.getProxy(repositoryDefinition.getRepositoryType(), repositoryDefinition.getProperties());

        String beanName = doRegister(repositoryDefinition.getRepositoryType(), repositoryInstance);

        typeRegisteredRepositories.put(repositoryDefinition.getRepositoryType(), repositoryInstance);
        nameRegisteredRepositories.put(beanName, repositoryDefinition);
        return beanName;
    }

    @Override
    public Collection<StandardJpaRepository> registeredRepositories() {
        return typeRegisteredRepositories.values();
    }

    @Override
    public StandardJpaRepository getRegisteredRepository(Class repositoryType) {
        return typeRegisteredRepositories.get(repositoryType);
    }

    @Override
    public boolean contains(Class repositoryType) {
        return typeRegisteredRepositories.containsKey(repositoryType);
    }

    @Override
    public StandardJpaRepository findRegisteredRepositoryOfEntity(Class entityType) {
        Optional<RepositoryDefinition> optional = nameRegisteredRepositories.values().stream()
                .filter(definition -> definition.getProperties().getEntityTable().getEntityType() == entityType)
                .findAny();
        return optional
                .map(repositoryDefinition -> getRegisteredRepository(repositoryDefinition.getRepositoryType()))
                .orElse(null);
    }


    /**
     * @param beanType
     * @param bean
     */
    @SuppressWarnings("unchecked")
    private String doRegister(Class beanType, Object bean) {

        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(beanType, () -> bean)
                .getBeanDefinition();
        beanDefinition.setSynthetic(true);
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        String generateBeanName = beanNameGenerator.generateBeanName(beanDefinition, beanFactory);

        beanFactory.registerBeanDefinition(generateBeanName, beanDefinition);

        logger.trace("register bean [{}], name [{}]", beanType.getName(), generateBeanName);

        return generateBeanName;
    }
}
