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
 * @description 读取请求
 */
public class ReadQueryRequest implements QueryRequest {

    protected final Query query;

    protected final TableProperties<?, ?>  tableProperties;

    public ReadQueryRequest(Query query, TableProperties<?, ?> tableProperties) {
        this.query = query;
        this.tableProperties = tableProperties;
    }

    @Override
    public String getSqlTemplate() {
        SqlTemplate sqlTemplate = new SqlTemplate("select ");
        sqlTemplate.setDistinct(query.isDistinct());
        sqlTemplate.setIncludeBy(query.getIncludeBy());
        sqlTemplate.setFromTable(tableProperties.getMetadata().getTableName());
        sqlTemplate.setWhere(query.getWhere());
        sqlTemplate.setOrderBy(query.getOrderBy());
        sqlTemplate.setPage(query.getQueryPage());
        return sqlTemplate.getSqlString();
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
