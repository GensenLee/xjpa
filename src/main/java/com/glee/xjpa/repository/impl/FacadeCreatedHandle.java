package com.glee.xjpa.repository.impl;

/**
 * @author GENSEN
 * @date 2022/12/6
 * @description facade handle, facade 创建后置处理
 */
@SuppressWarnings("rawtypes")
public interface FacadeCreatedHandle {

    StandardJpaRepositoryFacade handle(StandardJpaRepositoryFacade target);

}
