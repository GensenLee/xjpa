package com.glee.xjpa.table;

import cn.hutool.json.JSONUtil;
import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.exception.XJpaInitException;
import com.glee.xjpa.util.EntityUtil;
import com.glee.xjpa.util.TableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class LazyLoadTableFieldProvider implements TableFieldProvider {

    protected static final Logger log = LoggerFactory.getLogger(LazyLoadTableFieldProvider.class);


    private boolean loaded;

    private final List<TableFieldMetadata> tableFieldMetadataList;

    private final Class<?> repositoryClass;

    private final TableInitErrorHandler initErrorHandler;

    private final DataSourceManager dataSourceManager;

    private final ReentrantLock reentrantLock = new ReentrantLock();


    public LazyLoadTableFieldProvider(Class<?> repositoryClass, TableInitErrorHandler initErrorHandler, DataSourceManager dataSourceManager) {
        this.repositoryClass = repositoryClass;
        this.initErrorHandler = initErrorHandler;
        this.dataSourceManager = dataSourceManager;
        this.tableFieldMetadataList = new ArrayList<>();
    }


    @Override
    public List<TableFieldMetadata> get() {
        if (!loaded) {
            load();
        }
        return tableFieldMetadataList;
    }

    @Override
    public void load() {

        try {
            reentrantLock.lock();
            if (loaded) {
                return;
            }

            StopWatch stopWatch = new StopWatch("load table");
            stopWatch.start();

            tableFieldMetadataList.clear();
            tableFieldMetadataList.addAll(loadDatabaseTable(repositoryClass, TableUtil.getTableNameByRepositoryType(repositoryClass)));

            stopWatch.stop();
            log.trace("load table={} : {}", TableUtil.getTableNameByRepositoryType(repositoryClass), stopWatch.shortSummary());
            loaded = true;
        } finally {
            reentrantLock.unlock();
        }
    }

    private List<TableField> loadDatabaseTable(Class<?> repositoryClass, String tableName) {
        return loadDatabaseTable(repositoryClass, tableName, 0);
    }

    /**
     * 从数据库加载
     *
     * @param repositoryClass
     * @param tableName
     * @return
     */
    private List<TableField> loadDatabaseTable(Class<?> repositoryClass, String tableName, int retry) {
        if (retry > 3) {
            log.error("load table metadata fail after 3 retry");
            throw new XJpaInitException("load table [%s] error".formatted(tableName));
        }

        List<Field> fieldList = EntityUtil.getFields(TableField.class);

        Connection connection = dataSourceManager.getConnection(repositoryClass);
        try (PreparedStatement preparedStatement = connection.prepareStatement(String.format("show full columns from `%s`", tableName))){
            ResultSet resultSet = preparedStatement.executeQuery();
            List<TableField> result = new ArrayList<>();
            log.trace("table {} load start", tableName);
            while (resultSet.next()) {
                TableField tableField = new TableField();
                for (Field field : fieldList) {
                    field.setAccessible(true);
                    field.set(tableField, resultSet.getString(field.getName()));
                }
                result.add(tableField);
                log.trace("field {} [{}]", tableField.getField(), JSONUtil.toJsonStr(tableField));
            }
            resultSet.close();
            log.trace("table {} load end", tableName);
            return result;
        } catch (Exception e) {
            boolean handle = initErrorHandler.handle(repositoryClass, tableName, e);
            if (handle) {
                return loadDatabaseTable(repositoryClass, tableName, retry + 1);
            } else {
                throw new XJpaInitException(e);
            }
        } finally {
            dataSourceManager.releaseConnection(connection, repositoryClass);
        }

    }

}
