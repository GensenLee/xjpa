package com.glee.xjpa.io.update;

import com.glee.xjpa.io.QueryRequest;
import com.glee.xjpa.sql.UpdateSqlTemplate;
import com.glee.xjpa.sql.where.usermodel.QueryWhere;
import com.glee.xjpa.table.TableProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2026/3/31
 * @description 更新请求
 */
public class UpdateRequest implements QueryRequest {


    protected final TableProperties<?, ?> tableProperties;

    protected final UpdateSets updateSets;

    protected final QueryWhere where;

    public UpdateRequest(TableProperties<?, ?> tableProperties, UpdateSets updateSets, QueryWhere where) {
        this.tableProperties = tableProperties;
        this.updateSets = updateSets;
        this.where = where;
    }

    @Override
    public String getSqlTemplate() {
        return UpdateSqlTemplate.buildUpdateSql(tableProperties, updateSets, where);
    }

    @Override
    public Map<Integer, Object> getParameters() {
        Map<Integer, Object> result = new HashMap<>();
        final AtomicInteger index = new AtomicInteger(1);
        for (UpdateSetClause updateSetClause : updateSets.getUpdateFields()) {
            result.put(index.getAndIncrement(), updateSetClause.getParameter(tableProperties));
        }

        if (where == null || where.isEmpty()) {
            return result;
        }
        result.putAll(where.indexValues(index));
        return result;
    }
}