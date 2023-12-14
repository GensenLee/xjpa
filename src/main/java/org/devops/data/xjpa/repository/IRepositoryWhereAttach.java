package org.devops.data.xjpa.repository;

import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description 主对象条件设置
 */
public interface IRepositoryWhereAttach {


    IRepositoryWhereAttach where(boolean valid, IQueryWhereObject whereValue);

    IRepositoryWhereAttach where(IQueryWhereObject whereValue);

    IRepositoryWhereAttach where(String column, Object value);

    IRepositoryWhereAttach where(boolean valid, String column, Object value);

    IRepositoryWhereAttach where(String column, WhereOperator operator);

    IRepositoryWhereAttach where(boolean valid, String column, WhereOperator operator);

    IRepositoryWhereAttach where(String column, WhereOperator operator, Condition condition);

    IRepositoryWhereAttach where(boolean valid, String column, WhereOperator operator, Condition condition);

    IRepositoryWhereAttach where(String column, Object value, WhereOperator operator);

    IRepositoryWhereAttach where(boolean valid, String column, Object value, WhereOperator operator);

    IRepositoryWhereAttach where(String column, Object value, Condition condition);

    IRepositoryWhereAttach where(boolean valid, String column, Object value, Condition condition);

    IRepositoryWhereAttach where(String column, Object value, WhereOperator operator, Condition condition);

    IRepositoryWhereAttach where(boolean valid, String column, Object value, WhereOperator operator, Condition condition);

    void clear();
}
