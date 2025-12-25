package com.glee.xjpa.repository.impl;

/**
 * @author GENSEN
 * @date 2022/12/6
 * @description crud代理执行
 */
public interface CurdExecutor {

    <T> T execute(CurdCommand curdCommand);

}
