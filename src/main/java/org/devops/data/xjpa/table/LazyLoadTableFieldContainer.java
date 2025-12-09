package org.devops.data.xjpa.table;

import cn.hutool.json.JSONUtil;
import org.devops.data.xjpa.configuration.RepositoriesConfigurationManager;
import org.devops.data.xjpa.configuration.RepositoryProperties;
import org.devops.data.xjpa.exception.XjpaInitException;
import org.devops.data.xjpa.util.EntityUtil;
import org.devops.data.xjpa.util.TableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.StopWatch;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description 延迟加载的容器
 */
public class LazyLoadTableFieldContainer implements TableFieldContainer {

    protected static final Logger logger = LoggerFactory.getLogger(LazyLoadTableFieldContainer.class);


    private boolean loaded;

    private final List<TableFieldMetadata> tableFieldMetadataList;

    private final Class<?> repositoryType;

    private final RepositoriesConfigurationManager repositoriesConfigurationManager;

    private final DefaultListableBeanFactory beanFactory;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    public LazyLoadTableFieldContainer(Class<?> repositoryType, RepositoriesConfigurationManager repositoriesConfigurationManager, DefaultListableBeanFactory beanFactory) {
        this.repositoryType = repositoryType;
        this.repositoriesConfigurationManager = repositoriesConfigurationManager;
        this.beanFactory = beanFactory;
        this.tableFieldMetadataList = new ArrayList<>();
    }


    @Override
    public List<TableFieldMetadata> get() {
        if (!loaded) {
            load();
        }
        return tableFieldMetadataList;
    }

    public void load() {

        try {
            reentrantLock.lock();
            if (loaded) {
                return;
            }

            StopWatch stopWatch = new StopWatch("load table");
            stopWatch.start();

            tableFieldMetadataList.clear();
            tableFieldMetadataList.addAll(loadDatabaseTable(repositoryType, TableUtil.getTableNameByRepositoryType(repositoryType)));

            stopWatch.stop();
            logger.trace("load table={} : {}", TableUtil.getTableNameByRepositoryType(repositoryType), stopWatch.shortSummary());
            loaded = true;
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 从数据库加载
     *
     * @param repositoryType
     * @param tableName
     * @return
     */
    private List<TableField> loadDatabaseTable(Class<?> repositoryType, String tableName) {
        List<Field> fieldList = EntityUtil.getFields(TableField.class);

        Connection connection = null;
        try {
            connection = openConnection(repositoryType);
            PreparedStatement preparedStatement = connection.prepareStatement(String.format("show full columns from `%s`", tableName));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<TableField> result = new ArrayList<>();
            logger.trace("table {} load start", tableName);
            while (resultSet.next()) {
                TableField tableField = new TableField();
                for (Field field : fieldList) {
                    field.setAccessible(true);
                    field.set(tableField, resultSet.getString(field.getName()));
                }
                result.add(tableField);
                logger.trace("field {} [{}]", tableField.getField(), JSONUtil.toJsonStr(tableField));
            }
            logger.trace("table {} load end", tableName);
            return result;
        } catch (Exception e) {
            ObjectProvider<TableInitErrorHandler> beanProvider = beanFactory.getBeanProvider(TableInitErrorHandler.class);
            if (!beanProvider.stream().findAny().isPresent()) {
                throw new XjpaInitException(e);
            } else {
                TableInitErrorHandler errorHandler = beanProvider.getIfAvailable();
                assert errorHandler != null;
                boolean handle = errorHandler.handle(repositoryType, tableName,
                        repositoriesConfigurationManager.getRepositoryProperties(repositoryType), e);
                if (handle) {
                    return loadDatabaseTable(repositoryType, tableName);
                } else {
                    throw new XjpaInitException(e);
                }
            }
        } finally {
            closeConnection();
        }

    }

    /**
     * @param repositoryType
     * @return
     */
    private Connection openConnection(Class<?> repositoryType) {
        RepositoryProperties repositoryProperties = repositoriesConfigurationManager.getRepositoryProperties(repositoryType);
        return repositoryProperties.getDataSource().getConnection();
    }


    /**
     *
     */
    private void closeConnection() {
        RepositoryProperties repositoryProperties = repositoriesConfigurationManager.getRepositoryProperties(repositoryType);
        repositoryProperties.getDataSource().close();
    }

}
