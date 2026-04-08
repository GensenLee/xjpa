package com.glee.xjpa.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author GENSEN
 * @date 2026/1/20
 * @description xjpa
 */
@ConfigurationProperties(prefix = "xjpa")
public class XJpaProperties {

    private LogicDelete logicDelete = new LogicDelete();

    public static class LogicDelete {
        private boolean enabled = false;
        private String controlColumn;
        private String deletedValue = "1";
        private List<String> excludeTable;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getControlColumn() {
            return controlColumn;
        }

        public void setControlColumn(String controlColumn) {
            this.controlColumn = controlColumn;
        }

        public String getDeletedValue() {
            return deletedValue;
        }

        public void setDeletedValue(String deletedValue) {
            this.deletedValue = deletedValue;
        }

        public List<String> getExcludeTable() {
            return excludeTable;
        }

        public void setExcludeTable(List<String> excludeTable) {
            this.excludeTable = excludeTable;
        }
    }

    public LogicDelete getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(LogicDelete logicDelete) {
        this.logicDelete = logicDelete;
    }
}
