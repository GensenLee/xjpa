package org.devops.data.xjpa.repository.impl;

import org.devops.data.xjpa.sql.logger.SwitchableLogger;
import org.devops.data.xjpa.table.TableMetadata;

/**
 * @author GENSEN
 * @date 2022/11/18
 * @description logger控制器
 */
public interface RepositoryController extends SwitchableLogger {

    TableMetadata getTableFieldMetadata();

    void setAttribute(String key, Object attribute);

    Object getAttribute(String key);

    <K> K getNextId();

    RepositoryContext getContext();

}
