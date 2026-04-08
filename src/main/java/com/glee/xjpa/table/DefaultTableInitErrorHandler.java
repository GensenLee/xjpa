package com.glee.xjpa.table;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.annotation.XJpaTable;
import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.util.TableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

/**
 * @author GENSEN
 * @date 2023/1/4
 * @description 默认异常处理
 */
@SuppressWarnings("rawtypes")
public class DefaultTableInitErrorHandler implements TableInitErrorHandler {

    protected static final Logger log = LoggerFactory.getLogger(DefaultTableInitErrorHandler.class);

    private final DataSourceManager dataSourceManager;

    public DefaultTableInitErrorHandler(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }


    /**
     * @param repositoryClass
     * @param tableName
     * @param exception
     * @return
     */
    @Override
    public boolean handle(Class<?> repositoryClass, String tableName, Exception exception) {

        if (exception instanceof SQLSyntaxErrorException && ((SQLSyntaxErrorException) exception).getErrorCode() == 1146) {
            // 建表
            return createTable(repositoryClass, tableName);
        }

        return false;
    }


    private boolean createTable(Class<?> repositoryClass, String tableName) {
        Class tableEntityType = (Class) TableUtil.getTableEntityType(repositoryClass);
        XJpaTable XJpaTable = AnnotationUtils.findAnnotation(tableEntityType, XJpaTable.class);

        if (XJpaTable == null) {
            return false;
        }

        String createTableSql = XJpaTable.ddl();
        if (StrUtil.isEmpty(createTableSql) && StrUtil.isNotEmpty(XJpaTable.ddlPath())) {
            try {
                File file = ResourceUtils.getFile(XJpaTable.ddlPath());
                createTableSql = FileUtil.readString(file, StandardCharsets.UTF_8);
            } catch (FileNotFoundException e) {
                log.error("fail to create the table: {}", tableName, e);
                return false;
            }
        }

        if (StrUtil.isEmpty(createTableSql)) {
            log.info("table ddl not found");
            return false;
        }

        log.info("create table: {} use sql: {}", tableName, createTableSql);

        Connection connection = dataSourceManager.getConnection(repositoryClass);
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("create table error", e);
            return false;
        } finally {
            dataSourceManager.releaseConnection(connection, repositoryClass);
        }

        return true;
    }

}
