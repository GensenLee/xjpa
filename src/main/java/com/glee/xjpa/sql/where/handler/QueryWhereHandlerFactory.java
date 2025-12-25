package com.glee.xjpa.sql.where.handler;

/**
 * @author GENSEN
 * @date 2022/11/22
 * @description handler工厂
 */
public interface QueryWhereHandlerFactory {

    IQueryWhereHandler getHandler();

}
