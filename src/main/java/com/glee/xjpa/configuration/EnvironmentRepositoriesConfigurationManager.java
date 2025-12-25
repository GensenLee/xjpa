package com.glee.xjpa.configuration;

import com.glee.xjpa.datasource.LazyCacheSingleDataSource;
import com.glee.xjpa.datasource.RepositoryDataSource;
import com.glee.xjpa.exception.XjpaInitException;
import com.glee.xjpa.repository.StandardJpaRepository;
import com.glee.xjpa.table.EntityTable;
import com.glee.xjpa.table.LazyLoadTableFieldContainer;
import com.glee.xjpa.table.TableFieldContainer;
import com.glee.xjpa.util.TableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description environment控制配置
 */

@SuppressWarnings("rawtypes")
public class EnvironmentRepositoriesConfigurationManager extends AbstractRepositoriesConfigurationManager {

    protected static final Logger logger = LoggerFactory.getLogger(EnvironmentRepositoriesConfigurationManager.class);


    /**
     * repositoryType -> entityTable
     */
    private final Map<Type, EntityTable> entityTableCache;

    private final Map<Type, RepositoryProperties> repositoryPropertyCache;

    private final DefaultListableBeanFactory beanFactory;

    private final Environment environment;

    public EnvironmentRepositoriesConfigurationManager(RepositoryGlobalConfig repositoryGlobalConfig,
                                                       DefaultListableBeanFactory beanFactory,
                                                       Environment environment) {
        super(repositoryGlobalConfig);
        this.beanFactory = beanFactory;
        this.environment = environment;
        this.repositoryPropertyCache = new HashMap<>();
        this.entityTableCache = new HashMap<>();
    }


    @Override
    public RepositoryProperties getRepositoryProperties(Class repositoryType) {
        return repositoryPropertyCache.get(repositoryType);
    }

    /**
     * @param repositoryType
     * @return
     */
    private EntityTable getEntityTable(Class repositoryType) {
        EntityTable entityTable = entityTableCache.get(repositoryType);
        if (entityTable == null) {
            logger.error("can not find EntityTable in cache");
            throw new XjpaInitException("entity table init error");
        }
        return entityTable;
    }


    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public synchronized void refresh() {

        for (Class<? extends StandardJpaRepository> repositoryType : repositoryGlobalConfig.getRepositoryTypes()) {

            if (entityTableCache.containsKey(repositoryType)) {
                continue;
            }

            TableFieldContainer tableFieldContainer = new LazyLoadTableFieldContainer(repositoryType, this, beanFactory);

            entityTableCache.put(repositoryType, TableUtil.wrapEntityTable(repositoryType, tableFieldContainer));

            repositoryPropertyCache.put(repositoryType, createEntityTableProperty(repositoryType));
        }
    }

    /**
     * @param repositoryType
     * @return
     */
    private RepositoryProperties createEntityTableProperty(Class repositoryType) {
        EntityTable entityTable = getEntityTable(repositoryType);

        String dataSourceName = repositoryGlobalConfig.getDataSourceName(repositoryType.getPackage().getName());

        RepositoryDataSource dataSource = new LazyCacheSingleDataSource(dataSourceName);

        return new DefaultRepositoryProperties(entityTable, dataSource);
    }

    public DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
