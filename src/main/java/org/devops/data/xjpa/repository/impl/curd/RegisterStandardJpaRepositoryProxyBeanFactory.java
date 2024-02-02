package org.devops.data.xjpa.repository.impl.curd;

import org.devops.data.xjpa.configuration.RepositoriesConfigurationManager;
import org.devops.data.xjpa.configuration.EnvironmentRepositoriesConfigurationManager;
import org.devops.data.xjpa.configuration.RepositoryProperties;
import org.devops.data.xjpa.configuration.SpringApplicationContextHandle;
import org.devops.data.xjpa.exception.XjpaInitException;
import org.devops.data.xjpa.repository.IEnhanceCurdRepository;
import org.devops.data.xjpa.repository.impl.*;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.sql.logger.SwitchableSqlLogger;
import org.devops.data.xjpa.sql.result.parser.ResultParser;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description spring注册模式
 */
@Deprecated
@SuppressWarnings({"rawtypes", "unchecked"})
public class RegisterStandardJpaRepositoryProxyBeanFactory extends AbstractRepositoryProxyBeanFactory<StandardJpaRepositoryProxyImpl> {

    private final SqlLogger sqlLogger = new SwitchableSqlLogger();

    private final RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory;

    private final DefaultListableBeanFactory beanFactory;

    private final Environment environment;

    private final Map<Class, String> registeredRepositories;


    protected RegisterStandardJpaRepositoryProxyBeanFactory(RepositoriesConfigurationManager repositoriesConfigurationManager,
                                                            RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory) {
        super(repositoriesConfigurationManager);
        this.beanFactory = ((EnvironmentRepositoriesConfigurationManager) repositoriesConfigurationManager).getBeanFactory();
        this.environment = ((EnvironmentRepositoriesConfigurationManager) repositoriesConfigurationManager).getEnvironment();
        this.implProxyBeanFactoryFactory = implProxyBeanFactoryFactory;
        this.registeredRepositories = new HashMap<>();
    }



    /**
     * @param repositoryType
     * @return
     */
    private String register(Class repositoryType) {

        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                .rootBeanDefinition(StandardJpaRepositoryProxyImpl.class)
                .setScope(BeanDefinition.SCOPE_PROTOTYPE)
                .getBeanDefinition();

        String beanName = createBeanName(repositoryType);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);

        return beanName;
    }

    /**
     * @param repositoryType
     * @return
     */
    private String createBeanName(Class repositoryType) {
        return repositoryType.getSimpleName() + "@" + repositoryType.hashCode();
    }


    /**
     * @param repositoryType
     * @return
     */
    private String getBeanName(Class repositoryType) {
        return registeredRepositories.computeIfAbsent(repositoryType, this::register);
    }






    /**
     * 将bean注册到spring中，主要目的是托管事务
     * prototype 模式注册
     * 每次获取一个新的对象
     *
     * 控制好只注册一次
     *
     * @param repositoryType
     * @return
     */
    @Override
    public StandardJpaRepositoryProxyImpl getProxy(Class repositoryType) {

        String beanName = getBeanName(repositoryType);

        if (!beanFactory.containsBean(beanName)) {
            throw new XjpaInitException(String.format("repository register error, repositoryType=%s beanName=%s", repositoryType, beanName));
        }


        RepositoryProperties repositoryProperties = repositoriesConfigurationManager.getRepositoryProperties(repositoryType);


        RepositoryContextAttribute repositoryContextAttribute = new EnvironmentRepositoryContextAttribute(environment);

        ResultParser resultParser = SpringApplicationContextHandle.getApplicationContext().getBean(ResultParser.class);

        ObjectProvider<SqlLogger> objectProvider = beanFactory.getBeanProvider(SqlLogger.class);
        SqlLogger logger = objectProvider.stream()
                .findFirst()
                .orElse(sqlLogger);


        // 2022-11-18 通过spring容器注册支持事务管理，直接new实例化无法全局管理事务

        final StandardJpaRepositoryProxyImpl standardJpaRepositoryProxy = beanFactory.getBean(beanName,
                StandardJpaRepositoryProxyImpl.class,
                getKeyType(repositoryType),
                getEntityType(repositoryType),
                logger,
                repositoryProperties,
                resultParser,
                repositoryContextAttribute);


        prettyRepository(repositoryType, standardJpaRepositoryProxy);

        standardJpaRepositoryProxy.register((RepositoryContextObserver) repositoryContextAttribute);

        return standardJpaRepositoryProxy;
    }

    /**
     * 完整对象结构
     *
     * @param repositoryType
     * @param originalRepositoryProxy
     * @param <K>
     * @param <V>
     */
    private <K  extends Serializable, V> void prettyRepository(Class repositoryType, StandardJpaRepositoryProxyImpl<K, V> originalRepositoryProxy) {

        RepositoryProxyBeanFactory curdRepositoryProxyBeanFactory =
                implProxyBeanFactoryFactory.getFactory(IEnhanceCurdRepository.class, originalRepositoryProxy);


        originalRepositoryProxy.setEnhanceCurdRepository(doGetProxy(repositoryType, curdRepositoryProxyBeanFactory));
    }

    /**
     * @param repositoryType
     * @param proxyBeanFactory
     * @param <T>
     * @return
     */
    private <T> T doGetProxy(Class repositoryType, RepositoryProxyBeanFactory proxyBeanFactory) {
        return (T) proxyBeanFactory.getProxy(repositoryType);
    }
}
