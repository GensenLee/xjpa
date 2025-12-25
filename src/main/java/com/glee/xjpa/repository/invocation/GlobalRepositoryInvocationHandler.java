package com.glee.xjpa.repository.invocation;

/**
 * @author GENSEN
 * @date 2022/11/16
 * @description 全局调用过程管理
 */
public class GlobalRepositoryInvocationHandler {

    private final static ThreadLocal<RepositoryInvocation> REPOSITORY_INVOCATION_THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * @param invocation
     */
    public static void set(RepositoryInvocation invocation) {
        REPOSITORY_INVOCATION_THREAD_LOCAL.set(invocation);
    }

    /**
     * @return
     */
    public static RepositoryInvocation get() {
        return REPOSITORY_INVOCATION_THREAD_LOCAL.get();
    }


    public static void remove() {
        REPOSITORY_INVOCATION_THREAD_LOCAL.remove();
    }

}
