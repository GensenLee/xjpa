package com.glee.xjpa.io.join;

/**
 * @author GENSEN
 * @date 2026/1/9
 * @description 连表上下文
 */
public interface JoiningContext {

    void joinPointRegister(JoinPoint joinPoint);

    String getTableAlias(JoinPoint joinPoint);

    int getJoiningOrder(JoinPoint joinPoint);

}
