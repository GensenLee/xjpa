package com.glee.xjpa.io;

import com.glee.xjpa.sql.SqlTemplate;
import com.glee.xjpa.sql.where.usermodel.QueryWhereModel;
import com.glee.xjpa.table.TableProperties;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2026/3/24
 * @description 删除请求
 */
public class DeleteQueryRequest implements QueryRequest {

    protected final Query query;

    protected final TableProperties<?, ?>  tableProperties;

    public DeleteQueryRequest(Query query, TableProperties<?, ?> tableProperties) {
        this.query = query;
        this.tableProperties = tableProperties;
    }

    @Override
    public String getSqlTemplate() {
        return SqlTemplate.buildDeleteSql(tableProperties.getMetadata().getTableName(), query.getWhere());
    }

    @Override
    public Map<Integer, Object> getParameters() {
        QueryWhereModel<String> whereModel = query.getWhere();
        if (whereModel == null || whereModel.isEmpty()) {
            return Collections.emptyMap();
        }
        final AtomicInteger index = new AtomicInteger(1);
        return whereModel.indexValues(index);
    }
}