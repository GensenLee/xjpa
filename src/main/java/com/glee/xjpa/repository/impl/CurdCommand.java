package com.glee.xjpa.repository.impl;

/**
 * @author GENSEN
 * @date 2022/12/6
 * @description command
 */
public interface CurdCommand {

    <T> T execute() throws Exception;
}
