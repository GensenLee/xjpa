package org.devops.data.xjpa.table;

import lombok.extern.slf4j.Slf4j;
import org.devops.core.utils.util.ResourceUtil;
import org.devops.core.utils.util.StringUtil;
import org.devops.data.xjpa.annotation.TableSetting;
import org.devops.data.xjpa.configuration.RepositoryProperties;
import org.devops.data.xjpa.util.TableUtil;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

/**
 * @author GENSEN
 * @date 2023/1/4
 * @description 默认异常处理
 */
@Slf4j
@Component
public class DefaultTableInitErrorHandler implements TableInitErrorHandler {

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
        Class tableEntityType = (Class)TableUtil.getTableEntityType(repositoryType);
        TableSetting tableSetting = AnnotationUtils.findAnnotation(tableEntityType, TableSetting.class);

        if (tableSetting == null) {
            return false;
        }

        String createTableSql = tableSetting.ddl();
        if (StringUtil.isEmpty(createTableSql) && StringUtil.isNotEmpty(tableSetting.ddlPath())) {
            createTableSql = ResourceUtil.readResourceAsString(tableSetting.ddlPath()).trim();
        }

        if (StringUtil.isEmpty(createTableSql)) {
            log.info("table ddl not found");
            return false;
        }

        log.info("create table: {} use sql: {}", tableName, createTableSql);

        try (Connection connection = repositoryProperties.getDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(createTableSql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("create table error", e);
            return false;
        }

        return true;
    }

}
