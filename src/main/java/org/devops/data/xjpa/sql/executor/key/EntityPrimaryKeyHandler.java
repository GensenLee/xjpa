package org.devops.data.xjpa.sql.executor.key;

import org.devops.data.xjpa.table.identifier.IdentifierGenerator;

import java.sql.ResultSet;

/**
 * @author GENSEN
 * @date 2022/11/18
 * @description 主键管理
 */
public interface EntityPrimaryKeyHandler {

    /**
     * 写入数据生成的主键
     * @param generatedKeys
     * @return 是否有写入
     */
    boolean writeGeneratedKeys(ResultSet generatedKeys);

    /**
     * 写入主键生成器生成的主键
     * @param identifierGenerator
     * @return 是否有写入
     */
    boolean writeGeneratedKeys(IdentifierGenerator identifierGenerator);

}
