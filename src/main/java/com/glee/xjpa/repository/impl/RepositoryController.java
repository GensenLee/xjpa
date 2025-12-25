package com.glee.xjpa.repository.impl;

import com.glee.xjpa.sql.logger.SwitchableLogger;
import com.glee.xjpa.table.TableMetadata;

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
