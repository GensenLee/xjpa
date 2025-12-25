package com.glee.xjpa.repository.impl.curd;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.configuration.*;
import com.glee.xjpa.repository.impl.*;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;
import com.glee.xjpa.repository.impl.enhance.ThreadLocalEnhanceCurdBound;
import com.glee.xjpa.sql.where.handler.DefaultQueryWhereHandlerFactory;
import com.glee.xjpa.sql.where.handler.QueryWhereHandlerFactory;
import com.glee.xjpa.sql.where.handler.SoftDeleteConfig;
import com.glee.xjpa.sql.where.handler.SoftDeleteQueryWhereHandlerFactory;
import com.glee.xjpa.configuration.*;
import com.glee.xjpa.exception.XjpaInitException;
import com.glee.xjpa.repository.IEnhanceCurdRepository;
import com.glee.xjpa.repository.impl.*;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.sql.logger.SwitchableSqlLogger;
import com.glee.xjpa.sql.result.parser.FastjsonResultParser;
import com.glee.xjpa.sql.result.parser.ResultParser;
import com.glee.xjpa.table.EntityTable;
import com.glee.xjpa.table.identifier.DefaultIdentifierGeneratorFactory;
import com.glee.xjpa.table.identifier.IdentifierGenerator;
import com.glee.xjpa.table.identifier.IdentifierGeneratorFactory;
import com.glee.xjpa.table.identifier.IdentifierGeneratorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 默认
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultStandardJpaRepositoryProxyBeanFactory extends AbstractRepositoryProxyBeanFactory<StandardJpaRepositoryProxyImpl> {
    protected static final Logger logger = LoggerFactory.getLogger(StandardJpaRepositoryProxyImpl.class);

    private final RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory;

    private final DefaultListableBeanFactory beanFactory;

    protected DefaultStandardJpaRepositoryProxyBeanFactory(RepositoriesConfigurationManager repositoriesConfigurationManager,
                                                           DefaultListableBeanFactory beanFactory,
                                                           RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory) {
        super(repositoriesConfigurationManager);
        this.beanFactory = beanFactory;
        this.implProxyBeanFactoryFactory = implProxyBeanFactoryFactory;
    }


    @Override
    public StandardJpaRepositoryProxyImpl getProxy(Class repositoryType) {

        logger.trace("create standard jpa repository");

        RepositoryProperties repositoryProperties = repositoriesConfigurationManager.getRepositoryProperties(repositoryType);

        Environment environment = ((EnvironmentRepositoriesConfigurationManager) repositoriesConfigurationManager).getEnvironment();
        final RepositoryContextAttribute repositoryContextAttribute = new EnvironmentRepositoryContextAttribute(environment);

        ResultParser resultParser = getResultParser();

        SqlLogger sqlLogger = getSqlLogger(repositoryProperties);

        final StandardJpaRepositoryProxyImpl standardJpaRepositoryProxy = new StandardJpaRepositoryProxyImpl<>(
                getKeyType(repositoryType),
                getEntityType(repositoryType),
                sqlLogger,
                repositoryProperties,
                resultParser,
                repositoryContextAttribute,
                beanFactory);

        standardJpaRepositoryProxy.register((RepositoryContextObserver) repositoryContextAttribute);

        configRepository(repositoryType, repositoryProperties, standardJpaRepositoryProxy);

        prettyRepository(repositoryType, standardJpaRepositoryProxy);
        return standardJpaRepositoryProxy;
    }

    private final SqlLogger defaultSqlLogger = new SwitchableSqlLogger();

    private final ResultParser defaultResultParser = new FastjsonResultParser();

    /**
     * 获取有效的parser
     * @return
     */
    private ResultParser getResultParser() {
        ObjectProvider<ResultParser> beanProvider = SpringApplicationContextHandle.getApplicationContext().getBeanProvider(ResultParser.class);
        return beanProvider.stream()
                .findAny()
                .orElse(defaultResultParser);
    }


    /**
     * 获取有效得logger
     * @return
     */
    public SqlLogger getSqlLogger(RepositoryProperties repositoryProperties) {
        ObjectProvider<SqlLogger> beanProvider = SpringApplicationContextHandle.getApplicationContext().getBeanProvider(SqlLogger.class);
        Optional<SqlLogger> loggerOptional = beanProvider.stream().findAny();
        return loggerOptional.orElse(defaultSqlLogger);
    }

    private final IdentifierGeneratorType defaultType = IdentifierGeneratorType.SnowflakeTo36;

    private final IdentifierGeneratorFactory defaultIdentifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();

    private IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        ObjectProvider<IdentifierGeneratorFactory> beanProvider = SpringApplicationContextHandle.getApplicationContext().getBeanProvider(IdentifierGeneratorFactory.class);
        return beanProvider.stream()
                .findAny()
                .orElse(defaultIdentifierGeneratorFactory);
    }


    /**
     * 进一步配置
     *
     * @param repositoryType
     * @param repositoryProperties
     * @param originalRepositoryProxy
     * @param <K>
     * @param <V>
     */
    private <K extends Serializable, V> void configRepository(Class repositoryType, RepositoryProperties repositoryProperties,
                                                              StandardJpaRepositoryProxyImpl<K, V> originalRepositoryProxy) {
        EntityTable entityTable = repositoryProperties.getEntityTable();

        EnhanceCurdBound enhanceCurdBound = new ThreadLocalEnhanceCurdBound(originalRepositoryProxy);
        originalRepositoryProxy.setAttribute(EnhanceCurdBound.class.getName(), enhanceCurdBound);

        // 配置主键生成器
        if (entityTable.getPrimaryKeyField() != null && entityTable.getPrimaryKeyField().getGeneratedValue() != null) {

            String generator = StrUtil.emptyToDefault(entityTable.getPrimaryKeyField().getGeneratedValue().generator(), defaultType.name());
            // 默认使用 SnowflakeTo36
            IdentifierGeneratorType generatorType = StrUtil.isNotEmpty(generator) ? IdentifierGeneratorType.getInstance(generator) : defaultType;
            if (generatorType == null) {
                throw new XjpaInitException("unsupported generator: " + generator);
            }

            IdentifierGeneratorFactory identifierGeneratorFactory = getIdentifierGeneratorFactory();

            IdentifierGenerator<?> identifierGenerator = identifierGeneratorFactory.getGenerator(generatorType, entityTable.getTableName());

            originalRepositoryProxy.setAttribute(IdentifierGenerator.class.getName(), identifierGenerator);
        }

        // where handler factory

        RepositoryGlobalConfig globalConfig = repositoriesConfigurationManager.getGlobalConfig();
        SoftDeleteConfig softDeleteConfig = new SoftDeleteConfig(globalConfig, repositoryProperties);
        originalRepositoryProxy.setAttribute(SoftDeleteConfig.class.getName(), softDeleteConfig);

        logger.trace("repositoryType={} softDeleteConfig={}", repositoryType.getName(), softDeleteConfig);

        QueryWhereHandlerFactory queryWhereHandlerFactory;
        if (softDeleteConfig.isEnabled()) {
            queryWhereHandlerFactory = new SoftDeleteQueryWhereHandlerFactory(originalRepositoryProxy);
        }else {
            queryWhereHandlerFactory = new DefaultQueryWhereHandlerFactory(originalRepositoryProxy);
        }

        logger.trace("repositoryType={} set queryWhereHandlerFactory={}", repositoryType.getName(), queryWhereHandlerFactory.getClass());

        originalRepositoryProxy.setAttribute(QueryWhereHandlerFactory.class.getName(), queryWhereHandlerFactory);

    }




    /**
     * 完整对象结构
     *
     * @param repositoryType
     * @param originalRepositoryProxy
     * @param <K>
     * @param <V>
     */
    private <K extends Serializable, V> void prettyRepository(Class repositoryType, StandardJpaRepositoryProxyImpl<K, V> originalRepositoryProxy) {

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
