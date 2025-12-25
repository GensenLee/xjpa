package com.glee.xjpa.lifecycle;

import com.glee.xjpa.configuration.RepositoriesConfigurationManager;
import com.glee.xjpa.configuration.RepositoryGlobalConfig;
import com.glee.xjpa.configuration.RepositoryProperties;
import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.repository.impl.curd.StaticRepositoryProxyBeanFactoryFactory;
import com.glee.xjpa.constant.XjpaConstant;
import com.glee.xjpa.repository.StandardJpaRepository;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StopWatch;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description 功能应用生命周期
 */
@SuppressWarnings("rawtypes")
public class XjpaRepositoryAppLifecycle implements Lifecycle, ImportBeanDefinitionRegistrar, Ordered {
    protected static final Logger logger = LoggerFactory.getLogger(XjpaRepositoryAppLifecycle.class);

    private volatile boolean isRunning = false;
    private final RepositoryConfigSource configSource;
    private final RepositoryRegisterValidator repositoryRegisterValidator;
    private final Environment environment;
    XjpaRepositoryRegister register;
    private DefaultListableBeanFactory beanFactory;

    public XjpaRepositoryAppLifecycle(Environment environment) {
        this.environment = environment;
        this.configSource = new DefaultRepositoryConfigSource(environment);
        this.repositoryRegisterValidator = new DefaultRepositoryRegisterValidator(environment);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry,
                                        BeanNameGenerator importBeanNameGenerator) {
        LoggingSystem loggingSystem = ((DefaultListableBeanFactory) registry).getBean(LoggingSystem.class);
        loggingSystem.setLogLevel(Reflections.class.getName(), LogLevel.ERROR);

        beanFactory = (DefaultListableBeanFactory) registry;
        ((DefaultRepositoryConfigSource) configSource).setRegistry(beanFactory);
        configSource.refresh();
        StaticRepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory = new StaticRepositoryProxyBeanFactoryFactory(configSource.getConfigManager(), beanFactory);

        register = new DefaultXjpaRepositoryRegister(environment, beanFactory, implProxyBeanFactoryFactory);

        beanFactory.registerSingleton("XjpaRepositoryRegister", register);

        registerXjpa(this.getClass(), beanFactory, importBeanNameGenerator, this);

        StopWatch stopWatch = new StopWatch(getClass().getSimpleName());

        stopWatch.start("xjpa-register");
        start();
        stopWatch.stop();

        logger.trace(stopWatch.shortSummary());

    }

    /**
     * @param beanType
     * @param beanDefinitionRegistry
     * @param importBeanNameGenerator
     * @param bean
     */
    @SuppressWarnings("unchecked")
    private void registerXjpa(Class beanType, BeanDefinitionRegistry beanDefinitionRegistry,
                              BeanNameGenerator importBeanNameGenerator,
                              Object bean) {

        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(beanType, () -> bean)
                .getBeanDefinition();
        beanDefinition.setSynthetic(true);
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

        String generateBeanName = importBeanNameGenerator.generateBeanName(beanDefinition, beanDefinitionRegistry);
        generateBeanName += (XjpaConstant.POUND_MARK + bean.hashCode());
        beanDefinitionRegistry.registerBeanDefinition(generateBeanName, beanDefinition);

        logger.trace("register xjpa [{}], name [{}]", beanType.getName(), generateBeanName);
    }


    @Override
    public synchronized void start(){
        isRunning = true;

        scanBaseConfigPackage();

        logger.trace("xjpa running");
    }


    /**
     * 扫描配置的包
     */

    public void scanBaseConfigPackage() {

        RepositoryGlobalConfig globalConfig = configSource.getGlobalConfig();

        RepositoriesConfigurationManager repositoriesConfigurationManager = configSource.getConfigManager();

        for (Class<? extends StandardJpaRepository> type : globalConfig.getRepositoryTypes()) {
            if (register.contains(type)) {
                continue;
            }

            repositoryRegisterValidator.validate(type);

            RepositoryProperties repositoryProperties = repositoriesConfigurationManager.getRepositoryProperties(type);

            RepositoryDefinition repositoryDefinition = RepositoryDefinition.builder()
                    .withProperties(repositoryProperties)
                    .withRepositoryType(type)
                    .withRepositoryPackageName(ClassUtils.getPackageName(type))
                    .build();

            // 注册
            register.register(repositoryDefinition);
        }


    }



/*    public void scanBaseConfigPackage() {

        RepositoryGlobalConfig globalConfig = configSource.getGlobalConfig();
        for (String basePackage : globalConfig.baseRepositoryPackages()) {
            if (StringUtil.isEmpty(basePackage)) {
//                throw new CommonRuntimeException("ModelRepositoryConfig config error, repositoryBasePackage=" + basePackage);
                continue;
            }

            logger.debug("scan package {}", basePackage);

            Set<Class<? extends StandardJpaRepository>> subTypesOfModelRepository = ReflectionsUtil.getSubTypesOfStandardJpaRepository(basePackage);
            RepositoriesConfigurationManager repositoriesConfigurationManager = configSource.getConfigManager();

            for (Class<? extends StandardJpaRepository> type : subTypesOfModelRepository) {
                if (register.contains(type)) {
                    continue;
                }

                repositoryRegisterValidator.validate(type);

                RepositoryProperties repositoryProperties = repositoriesConfigurationManager.getRepositoryProperties(type);

                RepositoryDefinition repositoryDefinition = RepositoryDefinition.builder()
                        .withProperties(repositoryProperties)
                        .withRepositoryType(type)
                        .withRepositoryPackageName(basePackage)
                        .build();

                // 注册
                register.register(repositoryDefinition);
            }

            logger.debug("total register of package {}: {}", basePackage, subTypesOfModelRepository.size());
        }

    }*/

    /**
     * 扫描所有包
     */
/*    public void scanClassPath() {

        Reflections reflections = new Reflections();
        Set<Class<? extends StandardJpaRepository>> subTypesOfModelRepository = reflections.getSubTypesOf(StandardJpaRepository.class)
                .stream()
                // SkipRepositoryScan 为内部实现类
                .filter(clazz -> !clazz.isAnnotationPresent(SkipRepositoryScan.class))
                .collect(Collectors.toSet());

        RepositoriesConfigurationManager repositoriesConfigurationManager = configSource.getConfigManager();

        for (Class<? extends StandardJpaRepository> type : subTypesOfModelRepository) {
            if (register.contains(type)) {
                continue;
            }


            repositoryRegisterValidator.validate(type);

            RepositoryProperties repositoryProperties = repositoriesConfigurationManager.getRepositoryProperties(type);

            RepositoryDefinition repositoryDefinition = RepositoryDefinition.builder()
                    .withProperties(repositoryProperties)
                    .withRepositoryType(type)
                    .withRepositoryPackageName(ClassUtils.getPackageName(type))
                    .build();

            // 注册
            register.register(repositoryDefinition);
        }
    }*/

    @Override
    public synchronized void stop() {
        logger.trace("xjpa stopping");
        for (Object proxy : register.registeredRepositories()) {
            if (proxy instanceof RepositoryContext) {
                ((RepositoryContext) proxy).close();
            }
        }
        isRunning = false;
        logger.trace("xjpa stopped");
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
