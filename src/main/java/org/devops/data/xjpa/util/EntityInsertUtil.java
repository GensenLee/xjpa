package org.devops.data.xjpa.util;

import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.executor.key.EntityPrimaryKeyHandler;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.table.identifier.IdentifierGenerator;

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
