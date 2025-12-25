package com.glee.xjpa.repository.invocation;

import java.lang.reflect.Method;

/**
 * @author GENSEN
 * @date 2022/11/16
 * @description 调用
 */
public interface RepositoryInvocation {

    /**
     * @return 调用对象
     */
    Object callTarget();

    /**
     * @return 调用方法
     */
    Method callMethod();

    /**
     * @return 调用参数
     */
    Object[] callArgs();

    boolean isTransactionHandled();

}
