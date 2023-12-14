package org.devops.data.xjpa.repository.impl.curd;

import lombok.extern.slf4j.Slf4j;
import org.devops.core.utils.util.StringUtil;
import org.devops.data.xjpa.configuration.*;
import org.devops.data.xjpa.exception.XjpaInitException;
import org.devops.data.xjpa.repository.IEnhanceCurdRepository;
import org.devops.data.xjpa.repository.impl.*;
import org.devops.data.xjpa.repository.impl.enhance.EnhanceCurdBound;
import org.devops.data.xjpa.repository.impl.enhance.ThreadLocalEnhanceCurdBound;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.sql.logger.SwitchableSqlLogger;
import org.devops.data.xjpa.sql.result.parser.FastjsonResultParser;
import org.devops.data.xjpa.sql.result.parser.ResultParser;
import org.devops.data.xjpa.sql.where.handler.DefaultQueryWhereHandlerFactory;
import org.devops.data.xjpa.sql.where.handler.QueryWhereHandlerFactory;
import org.devops.data.xjpa.sql.where.handler.SoftDeleteConfig;
import org.devops.data.xjpa.sql.where.handler.SoftDeleteQueryWhereHandlerFactory;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.table.identifier.DefaultIdentifierGeneratorFactory;
import org.devops.data.xjpa.table.identifier.IdentifierGenerator;
import org.devops.data.xjpa.table.identifier.IdentifierGeneratorFactory;
import org.devops.data.xjpa.table.identifier.IdentifierGeneratorType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 默认
 */
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultStandardJpaRepositoryProxyBeanFactory extends AbstractRepositoryProxyBeanFactory<StandardJpaRepositoryProxyImpl> {

    private final RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory;

    protected DefaultStandardJpaRepositoryProxyBeanFactory(RepositoriesConfigurationManager repositoriesConfigurationManager,
                                                           RepositoryProxyBeanFactoryFactory implProxyBeanFactoryFactory) {
        super(repositoriesConfigurationManager);
        this.implProxyBeanFactoryFactory = implProxyBeanFactoryFactory;
    }


    @Override
    public StandardJpaRepositoryProxyImpl getProxy(@NotNull Class repositoryType) {

        log.trace("create standard jpa repository");

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
                repositoryContextAttribute);

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

            String generator = StringUtil.getDefault(entityTable.getPrimaryKeyField().getGeneratedValue().generator(), defaultType.name());
            // 默认使用 SnowflakeTo36
            IdentifierGeneratorType generatorType = StringUtil.isNotEmpty(generator) ? IdentifierGeneratorType.getInstance(generator) : defaultType;
            if (generatorType == null) {
                throw new XjpaInitException("unsupported generator: " + IdentifierGeneratorType.getInstance(entityTable.getPrimaryKeyField().getGeneratedValue().generator()));
            }

            IdentifierGeneratorFactory identifierGeneratorFactory = getIdentifierGeneratorFactory();

            IdentifierGenerator<?> identifierGenerator = identifierGeneratorFactory.getGenerator(generatorType, entityTable.getTableName());

            originalRepositoryProxy.setAttribute(IdentifierGenerator.class.getName(), identifierGenerator);
        }

        // where handler factory

        RepositoryGlobalConfig globalConfig = repositoriesConfigurationManager.getGlobalConfig();
        SoftDeleteConfig softDeleteConfig = new SoftDeleteConfig(globalConfig, repositoryProperties);
        originalRepositoryProxy.setAttribute(SoftDeleteConfig.class.getName(), softDeleteConfig);

        log.trace("repositoryType={} softDeleteConfig={}", repositoryType.getName(), softDeleteConfig);

        QueryWhereHandlerFactory queryWhereHandlerFactory;
        if (softDeleteConfig.isEnabled()) {
            queryWhereHandlerFactory = new SoftDeleteQueryWhereHandlerFactory(originalRepositoryProxy);
        }else {
            queryWhereHandlerFactory = new DefaultQueryWhereHandlerFactory(originalRepositoryProxy);
        }

        log.trace("repositoryType={} set queryWhereHandlerFactory={}", repositoryType.getName(), queryWhereHandlerFactory.getClass());

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
