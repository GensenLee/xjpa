package com.glee.xjpa.repository.impl.curd;

import com.glee.xjpa.configuration.RepositoriesConfigurationManager;
import com.glee.xjpa.repository.IXjpaRepository;
import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.repository.impl.RepositoryProxyBeanFactory;
import com.glee.xjpa.util.TableUtil;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 默认
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractRepositoryProxyBeanFactory<T extends IXjpaRepository> implements RepositoryProxyBeanFactory<T> {
    protected final RepositoriesConfigurationManager repositoriesConfigurationManager;

    protected RepositoryContext context;

    protected AbstractRepositoryProxyBeanFactory(RepositoriesConfigurationManager repositoriesConfigurationManager) {
        this.repositoriesConfigurationManager = repositoriesConfigurationManager;
    }

    /**
     * @param repositoryType
     * @return
     */
    public Class getKeyType(Class repositoryType) {
        return (Class) TableUtil.getTableKeyType(repositoryType);
    }

    /**
     * @param repositoryType
     * @return
     */
    public Class getEntityType(Class repositoryType) {
        return (Class) TableUtil.getTableEntityType(repositoryType);
    }


    @Override
    public void bindContext(RepositoryContext context) {
        this.context = context;
    }
}
