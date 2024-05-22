package org.devops.data.xjpa.sql.logger;

/**
 * @author GENSEN
 * @date 2022/11/18
 * @description 可切换状态的
 */
public interface SwitchableLogger {

    void closeLog();

    void openLog();

}
