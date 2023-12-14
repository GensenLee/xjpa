package org.devops.data.xjpa.sql.executor.query;

import lombok.Getter;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.executor.LimitHandler;
import org.devops.data.xjpa.sql.executor.SortHandler;
import org.devops.data.xjpa.sql.executor.UpdateValueHandler;

import java.util.Set;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 根据where更新条件
 */
@Getter
public class UpdateByWhereQueryRequest<K, V> extends AbstractQueryRequest<K, V> {

    /**
     * 分页限位
     */
    final LimitHandler limitHandler;

    /**
     * 排序
     */
    final SortHandler sortHandler;

    /**
     *
     */
    final UpdateValueHandler updateValueHandler;


    public UpdateByWhereQueryRequest(RepositoryContext<K, V> context,
                                     LimitHandler limitHandler,
                                     SortHandler sortHandler,
                                     UpdateValueHandler updateValueHandler) {
        super(context);
        this.limitHandler = limitHandler;
        this.sortHandler = sortHandler;
        this.updateValueHandler = updateValueHandler;
    }

    public Set<String> includeColumns() {
        return null;
    }



}
