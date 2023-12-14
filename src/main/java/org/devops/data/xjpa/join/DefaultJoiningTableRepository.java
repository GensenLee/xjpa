package org.devops.data.xjpa.join;

import org.devops.data.xjpa.repository.StandardJpaRepository;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description
 */
@SuppressWarnings("rawtypes")
public class DefaultJoiningTableRepository implements JoiningTableRepository {

    protected final StandardJpaRepository repository;

    public DefaultJoiningTableRepository(StandardJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public JoinModel leftJoin(Class<?> rightEntityType) {
        return new MultipleJoinModel(repository, JoinType.LEFT_JOIN, rightEntityType);
    }

    @Override
    public JoinModel rightJoin(Class<?> rightEntityType) {
        return new MultipleJoinModel(repository, JoinType.RIGHT_JOIN, rightEntityType);
    }

    @Override
    public JoinModel innerJoin(Class<?> rightEntityType) {
        return new MultipleJoinModel(repository, JoinType.INNER_JOIN, rightEntityType);
    }
}
