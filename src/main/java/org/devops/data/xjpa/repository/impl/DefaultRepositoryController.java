package org.devops.data.xjpa.repository.impl;

import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.devops.data.xjpa.repository.impl.proxy.RepositoryInvocationHandler;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.sql.logger.SwitchableLogger;
import org.devops.data.xjpa.table.TableMetadata;
import org.devops.data.xjpa.table.identifier.IdentifierGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author GENSEN
 * @date 2022/11/19
 * @description repository控制器
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultRepositoryController implements RepositoryController {

    private final Object repository;

    /**
     * 实际接收的的对象
     */
    private final Object actualRepository;

    public <T extends StandardJpaRepository> DefaultRepositoryController(T repository) {
        this.repository = repository;
        if (Proxy.isProxyClass(repository.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(repository);
            actualRepository = ((RepositoryInvocationHandler) invocationHandler).getStandardJpaRepository();
        }else {
            actualRepository = repository;
        }
    }


    @Override
    public void setAttribute(String key, Object attribute) {

        Object target = getTarget();

        if (!(target instanceof RepositoryContextAttribute)) {
            return;
        }

        ((RepositoryContextAttribute) target).setAttribute(key, attribute);
    }

    @Override
    public Object getAttribute(String key) {
        Object target = getTarget();

        if (!(target instanceof RepositoryContextAttribute)) {
            return null;
        }

        return  ((RepositoryContextAttribute) target).getAttribute(key);
    }

    @Override
    public <K> K getNextId() {
        Object target = getTarget();

        if (target instanceof RepositoryContext) {
            IdentifierGenerator identifierGenerator = ((RepositoryContext<?, ?>) target).getSingleton(IdentifierGenerator.class);
            return (K) identifierGenerator.next();
        }

        return null;
    }

    @Override
    public RepositoryContext getContext() {
        Object target = getTarget();

        if (target instanceof RepositoryContext) {
            return ((RepositoryContext<?, ?>) target);
        }

        throw new IllegalStateException("should not reach here");
    }

    private Object getTarget() {
        Object target;
        if (actualRepository instanceof Facade) {
            target = ((Facade) actualRepository).getActual();
        }else {
            target = actualRepository;
        }
        return target;
    }

    @Override
    public TableMetadata getTableFieldMetadata() {
        Object target = getTarget();

        RepositoryContext context = (RepositoryContext) target;

        return context.getEntityTable();
    }

    @Override
    public void closeLog() {
        Object target = getTarget();

        RepositoryContext context = (RepositoryContext) target;

        SqlLogger sqlLogger = context.localSqlLogger();

        if (sqlLogger instanceof SwitchableLogger) {
            ((SwitchableLogger) sqlLogger).closeLog();
        }
    }

    @Override
    public void openLog() {
        Object target = getTarget();

        RepositoryContext context = (RepositoryContext) target;

        SqlLogger sqlLogger = context.localSqlLogger();

        if (sqlLogger instanceof SwitchableLogger) {
            ((SwitchableLogger) sqlLogger).openLog();
        }
    }
}
