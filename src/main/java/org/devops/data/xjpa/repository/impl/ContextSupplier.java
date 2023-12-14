package org.devops.data.xjpa.repository.impl;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description
 */
public interface ContextSupplier {

    @SuppressWarnings({"rawtypes"})
    RepositoryContext getContext();

}
