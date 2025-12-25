package com.glee.xjpa.util;

import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.table.EntityTable;
import com.glee.xjpa.table.identifier.IdentifierGenerator;
import com.glee.xjpa.sql.executor.key.EntityPrimaryKeyHandler;

import java.sql.ResultSet;
import java.util.function.Consumer;

/**
 * @author GENSEN
 * @date 2022/11/21
 * @description insert工具
 */
public class EntityInsertUtil {

    /**
     * @param context
     * @return
     */
    @SuppressWarnings({"rawtypes"})
    public static Consumer<ResultSet> generatedKeysOrGetKeysConsumer(RepositoryContext context) {
        EntityTable entityTable = context.getEntityTable();

        EntityPrimaryKeyHandler entityPrimaryKeyHandler = context.getSingleton(EntityPrimaryKeyHandler.class);

        Consumer<ResultSet> generatedKeysConsumer = resultSet -> {};
        if (entityPrimaryKeyHandler == null) {
            return generatedKeysConsumer;
        }

        if (entityTable.isAutoincrement()) {
            generatedKeysConsumer = entityPrimaryKeyHandler::writeGeneratedKeys;
        }else {
            IdentifierGenerator identifierGenerator = context.getSingleton(IdentifierGenerator.class);
            entityPrimaryKeyHandler.writeGeneratedKeys(identifierGenerator);
        }
        return generatedKeysConsumer;
    }

}
