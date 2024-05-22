package org.devops.data.xjpa.table.identifier;

/**
 * @author GENSEN
 * @date 2022/11/18
 * @description
 */
public interface IdentifierGeneratorFactory {

    /**
     * @param identifierGeneratorType
     * @param key
     * @return
     */
    IdentifierGenerator<?> getGenerator(IdentifierGeneratorType identifierGeneratorType, String key);
}
