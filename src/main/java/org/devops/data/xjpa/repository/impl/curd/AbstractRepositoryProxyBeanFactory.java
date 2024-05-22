package org.devops.data.xjpa.repository.impl.curd;

import org.devops.data.xjpa.configuration.RepositoriesConfigurationManager;
import org.devops.data.xjpa.repository.IXjpaRepository;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.repository.impl.RepositoryProxyBeanFactory;
import org.devops.data.xjpa.util.TableUtil;

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
