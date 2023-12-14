package org.devops.data.xjpa.repository.impl.curd;

import lombok.extern.slf4j.Slf4j;
import org.devops.core.utils.util.StringUtil;
import org.devops.data.xjpa.repository.IRepositoryWhereAttach;
import org.devops.data.xjpa.sql.where.XQueryWhere;
import org.devops.data.xjpa.sql.where.XjpaQueryWhere;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;
import org.devops.data.xjpa.sql.where.usermodel.XQueryWhereValue;

/**
 * @author GENSEN
 * @date 2022/11/2
 * @description 条件处理
 */
@Slf4j
public class RepositoryWhereLocal<K, V> implements IRepositoryWhereAttach {

    /**
     * 内部条件
     */
    private final ThreadLocal<XQueryWhere> modelWhereCompositeValueThreadLocal = ThreadLocal.withInitial(XjpaQueryWhere::new);

    /**
     * @return 当前过程的条件对象
     */
    XQueryWhere localQueryWhere() {
        return modelWhereCompositeValueThreadLocal.get();
    }

    @Override
    public RepositoryWhereLocal<K, V> where(boolean valid, IQueryWhereObject where) {
        if (valid) {
            where(where);
        }
        return this;
    }

    @Override
    public RepositoryWhereLocal<K, V> where(IQueryWhereObject where) {
        localQueryWhere().add(where);
        return this;
    }

    @Override
    public RepositoryWhereLocal<K, V> where(String column, Object value) {
        return where(column, value, WhereOperator.EQ, Condition.AND);
    }

    @Override
    public RepositoryWhereLocal<K, V> where(boolean valid, String column, Object value) {
        if (valid) {
            return where(column, value);
        }
        return this;
    }

    @Override
    public RepositoryWhereLocal<K, V> where(String column, WhereOperator operator) {
        where(column, null, operator, Condition.AND);
        return this;
    }

    @Override
    public RepositoryWhereLocal<K, V> where(boolean valid, String column, WhereOperator operator) {
        if (valid) {
            where(column, operator);
        }
        return this;
    }

    @Override
    public RepositoryWhereLocal<K, V> where(String column, WhereOperator operator, Condition condition) {
        switch (operator) {
            case IS_NOT_NULL:case IS_NULL:
                return where(column, null, operator, condition);
            default:
        }
        return this;
    }

    @Override
    public RepositoryWhereLocal<K, V> where(boolean valid, String column, WhereOperator operator, Condition condition) {
        if (valid) {
            where(column, operator, condition);
        }
        return this;
    }

    @Override
    public RepositoryWhereLocal<K, V> where(String column, Object value, WhereOperator operator) {
        return where(column, value, operator, Condition.AND);
    }

    @Override
    public RepositoryWhereLocal<K, V> where(boolean valid, String column, Object value, WhereOperator operator) {
        if (valid) {
            where(column, value, operator);
        }
        return this;
    }

    @Override
    public RepositoryWhereLocal<K, V> where(String column, Object value, Condition condition) {
        return where(column, value, WhereOperator.EQ, condition);
    }

    @Override
    public RepositoryWhereLocal<K, V> where(boolean valid, String column, Object value, Condition condition) {
        if (valid) {
            where(column, value, condition);
        }
        return this;
    }

    @Override
    public RepositoryWhereLocal<K, V> where(String column, Object value, WhereOperator operator, Condition condition) {
        if (StringUtil.isEmpty(column)) {
            return this;
        }
        switch (operator) {
            case IS_NOT_NULL:case IS_NULL:
                localQueryWhere().add(new XQueryWhereValue(column, value, operator, condition));
            default:
                if (value == null) {
                    return this;
                }
                localQueryWhere().add(new XQueryWhereValue(column, value, operator, condition));
        }

        return this;
    }

    @Override
    public RepositoryWhereLocal<K, V> where(boolean valid, String column, Object value, WhereOperator operator, Condition condition) {
        if (valid) {
            where(column, value, operator, condition);
        }
        return this;
    }

    @Override
    public void clear() {
        localQueryWhere().clear();
    }

    void clearLocalQueryWhere() {
        modelWhereCompositeValueThreadLocal.remove();
        log.trace("clean local where");
    }
}
