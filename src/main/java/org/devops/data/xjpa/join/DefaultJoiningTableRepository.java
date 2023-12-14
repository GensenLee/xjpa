package org.devops.data.xjpa.join;

import org.devops.data.xjpa.lifecycle.XjpaRepositoryRegister;
import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description
 */
@SuppressWarnings("rawtypes")
public class DefaultJoiningTableRepository implements JoiningTableRepository {

    protected final StandardJpaRepository repository;

    protected final DefaultListableBeanFactory beanFactory;

    public DefaultJoiningTableRepository(StandardJpaRepository repository, DefaultListableBeanFactory beanFactory) {
        this.repository = repository;
        this.beanFactory = beanFactory;
    }

    @Override
    public JoinModel leftJoin(Class<?> rightEntityType) {
        XjpaRepositoryRegister register = beanFactory.getBean(XjpaRepositoryRegister.class);
        return new MultipleJoinModel(repository, JoinType.LEFT_JOIN, rightEntityType, register);
    }

    @Override
    public JoinModel rightJoin(Class<?> rightEntityType) {
        XjpaRepositoryRegister register = beanFactory.getBean(XjpaRepositoryRegister.class);
        return new MultipleJoinModel(repository, JoinType.RIGHT_JOIN, rightEntityType, register);
    }

    @Override
    public JoinModel innerJoin(Class<?> rightEntityType) {
        XjpaRepositoryRegister register = beanFactory.getBean(XjpaRepositoryRegister.class);
        return new MultipleJoinModel(repository, JoinType.INNER_JOIN, rightEntityType, register);
    }
}
