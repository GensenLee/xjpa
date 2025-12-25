package com.glee.xjpa.sql.where.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.glee.xjpa.annotation.TableSetting;
import com.glee.xjpa.configuration.RepositoryGlobalConfig;
import com.glee.xjpa.configuration.RepositoryProperties;
import com.glee.xjpa.exception.XjpaExecuteException;
import com.glee.xjpa.table.TableFieldMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/19
 * @description 逻辑删除
 */

@SuppressWarnings({"unchecked"})
public class SoftDeleteConfig {

    protected static final Logger logger = LoggerFactory.getLogger(SoftDeleteConfig.class);


    private final boolean enabled;

    private final String column;

    private final String deletedValue;

    private final String notDeleteValue;


    public SoftDeleteConfig(RepositoryGlobalConfig globalConfig, RepositoryProperties properties) {
        this.enabled = enabled(globalConfig, properties);
        if (enabled) {
            this.column = controlColumn(globalConfig, properties);
            this.deletedValue = deletedValue(globalConfig);
            this.notDeleteValue = notDeleteValue(globalConfig);
        } else {
            this.column = null;
            this.deletedValue = null;
            this.notDeleteValue = null;
        }
    }

    /**
     * @return 是否开启逻辑删除
     */
    private boolean enabled(RepositoryGlobalConfig globalConfig, RepositoryProperties properties) {
        TableSetting tableSetting = AnnotationUtils.findAnnotation(properties.getEntityTable().getEntityType(), TableSetting.class);

        if (tableSetting != null && tableSetting.softDelete()) {
            return true;
        }

        String enabled = StrUtil.toString(globalConfig.getProperty(RepositoryGlobalConfig.SOFT_DELETE_ENABLED));

        return Boolean.parseBoolean(enabled);
    }

    /**
     * @return 控制列
     */
    private String controlColumn(RepositoryGlobalConfig globalConfig, RepositoryProperties properties) {
        String column = StrUtil.toString(globalConfig.getProperty(RepositoryGlobalConfig.SOFT_DELETE_COLUMN));
        if (StrUtil.isEmpty(column)) {
            throw new XjpaExecuteException("column config required when soft delete enabled, config key :" + RepositoryGlobalConfig.SOFT_DELETE_COLUMN);
        }
        // 不需要检查Entity中是否存在该字段，只需要数据库表中存在即可
        List<TableFieldMetadata> tableFieldMetadataList = properties.getEntityTable().tableFields();
        if (tableFieldMetadataList.stream()
                .noneMatch(tableFieldMetadata -> tableFieldMetadata.getField().equals(column))) {
            String tableName = properties.getEntityTable().getTableName();
            logTableView(tableFieldMetadataList, tableName);
            throw new XjpaExecuteException(String.format("soft delete column [%s] of in table [%s] does not exist, config key : %s",
                    tableName,
                    column, RepositoryGlobalConfig.SOFT_DELETE_COLUMN));
        }

//        return (column.startsWith("`") ? "" : "`") + column + (column.endsWith("`") ? "" : "`");
        return column;
    }

    private void logTableView(List<TableFieldMetadata> tableFieldMetadataList, String tableName) {
        int index = 0;
        logger.trace("Table View[{}] start", tableName);
        for (TableFieldMetadata tableFieldMetadata : tableFieldMetadataList) {
            logger.warn("Field{} >>> [{}]", ++index, JSONUtil.toJsonStr(tableFieldMetadata));
        }
        logger.trace("Table View[{}] end", tableName);
    }

    /**
     * @return 已删除的标记值
     */
    private String deletedValue(RepositoryGlobalConfig globalConfig) {
        return StrUtil.emptyToDefault(globalConfig.getProperty(RepositoryGlobalConfig.SOFT_DELETE_DELETED_VALUE), "1");
    }

    /**
     * @return 未删除的标记值
     */
    private String notDeleteValue(RepositoryGlobalConfig globalConfig) {
        return StrUtil.emptyToDefault(globalConfig.getProperty(RepositoryGlobalConfig.SOFT_DELETE_NOT_DELETE_VALUE), "0");
    }


    public boolean isEnabled() {
        return enabled;
    }

    public String getColumn() {
        return column;
    }

    public String getDeletedValue() {
        return deletedValue;
    }

    public String getNotDeleteValue() {
        return notDeleteValue;
    }
}
