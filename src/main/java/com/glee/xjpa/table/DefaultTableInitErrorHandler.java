package com.glee.xjpa.table;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.annotation.TableSetting;
import com.glee.xjpa.configuration.RepositoryProperties;
import com.glee.xjpa.util.TableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
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
@Component
public class DefaultTableInitErrorHandler implements TableInitErrorHandler {

    protected static final Logger logger = LoggerFactory.getLogger(DefaultTableInitErrorHandler.class);


    /**
     * @param repositoryType
     * @param tableName
     * @param repositoryProperties
     * @param exception
     * @return
     */
    @Override
    public boolean handle(Class<?> repositoryType, String tableName, RepositoryProperties repositoryProperties,
                          Exception exception) {

        if (exception instanceof SQLSyntaxErrorException && ((SQLSyntaxErrorException) exception).getErrorCode() == 1146) {
            // 建表
            return createTable(repositoryType, tableName, repositoryProperties);
        }

        return false;
    }


    private boolean createTable(Class<?> repositoryType, String tableName, RepositoryProperties repositoryProperties) {
        Class tableEntityType = (Class) TableUtil.getTableEntityType(repositoryType);
        TableSetting tableSetting = AnnotationUtils.findAnnotation(tableEntityType, TableSetting.class);

        if (tableSetting == null) {
            return false;
        }

        String createTableSql = tableSetting.ddl();
        if (StrUtil.isEmpty(createTableSql) && StrUtil.isNotEmpty(tableSetting.ddlPath())) {
            try {
                File file = ResourceUtils.getFile(tableSetting.ddlPath());
                createTableSql = FileUtil.readString(file, StandardCharsets.UTF_8);
            } catch (FileNotFoundException e) {
                logger.error("create table", e);
                return false;
            }
        }

        if (StrUtil.isEmpty(createTableSql)) {
            logger.info("table ddl not found");
            return false;
        }

        logger.info("create table: {} use sql: {}", tableName, createTableSql);

        try (Connection connection = repositoryProperties.getDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(createTableSql);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error("create table error", e);
            return false;
        }

        return true;
    }

}
