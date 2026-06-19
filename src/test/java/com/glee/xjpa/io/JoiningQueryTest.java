package com.glee.xjpa.io;

import com.glee.xjpa.io.column.PlainTableColumn;
import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.io.groupby.JoiningGroupBy;
import com.glee.xjpa.io.groupby.JoiningGroupByMultipleColumn;
import com.glee.xjpa.io.groupby.JoiningGroupBySingleColumn;
import com.glee.xjpa.io.include.JoiningIncludeBy;
import com.glee.xjpa.io.include.JoiningIncludeMultipleColumn;
import com.glee.xjpa.io.include.JoiningIncludeSingleColumn;
import com.glee.xjpa.io.orderby.*;
import com.glee.xjpa.sql.where.usermodel.JoiningQueryWhere;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * JoiningQuery 连表查询描述对象测试
 * 覆盖连表场景下的 where/include/orderBy/distinct/limit/groupBy 等
 */
public class JoiningQueryTest {

    // 测试辅助方法：构造一个简单的 TableColumn
    private TableColumn col(String label) {
        return new PlainTableColumn(label);
    }

    // ========== JoiningIncludeBy 简单测试 ==========

    @Test
    public void testJoiningIncludeSingleColumn() {
        JoiningIncludeBy include = new JoiningIncludeSingleColumn(col("u.id"));
        assertEquals("u.id", include.toSqlClause());
    }

    @Test
    public void testJoiningIncludeMultipleColumnVarargs() {
        JoiningIncludeBy include = new JoiningIncludeMultipleColumn(
                col("u.id"),
                col("u.name"),
                col("o.amount")
        );
        String sql = include.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.contains("u.id"));
        assertTrue(sql.contains("u.name"));
        assertTrue(sql.contains("o.amount"));
    }

    @Test
    public void testJoiningIncludeMultipleColumnList() {
        JoiningIncludeBy include = new JoiningIncludeMultipleColumn(Arrays.asList(
                col("u.id"),
                col("u.name")
        ));
        String sql = include.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.contains("u.id"));
        assertTrue(sql.contains("u.name"));
    }

    // ========== JoiningOrderBy 简单测试 ==========

    @Test
    public void testJoiningOrderByAsc() {
        JoiningOrderBy orderBy = new JoiningOrderByAsc(col("u.name"));
        assertEquals("u.name asc", orderBy.toSqlClause());
    }

    @Test
    public void testJoiningOrderByDesc() {
        JoiningOrderBy orderBy = new JoiningOrderByDesc(col("o.created_at"));
        assertEquals("o.created_at desc", orderBy.toSqlClause());
    }

    @Test
    public void testJoiningOrderByMultipleColumn() {
        JoiningOrderBy orderBy = new JoiningOrderByMultipleColumn(
                new JoiningOrderByAsc(col("u.status")),
                new JoiningOrderByDesc(col("o.created_at"))
        );
        String sql = orderBy.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.contains("u.status asc"));
        assertTrue(sql.contains("o.created_at desc"));
    }

    @Test
    public void testJoiningOrderByMultipleColumnList() {
        JoiningOrderBy orderBy = new JoiningOrderByMultipleColumn(Arrays.asList(
                new JoiningOrderByAsc(col("u.status")),
                new JoiningOrderByAsc(col("u.name")),
                new JoiningOrderByDesc(col("o.created_at"))
        ));
        String sql = orderBy.toSqlClause();
        assertNotNull(sql);
    }

    // ========== JoiningGroupBy 简单测试 ==========

    @Test
    public void testJoiningGroupBySingleColumn() {
        JoiningGroupBy groupBy = new JoiningGroupBySingleColumn(col("u.country"));
        assertEquals("u.country", groupBy.toSqlClause());
    }

    @Test
    public void testJoiningGroupByMultipleColumn() {
        JoiningGroupBy groupBy = new JoiningGroupByMultipleColumn(col("u.country"), col("u.city"));
        String sql = groupBy.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.contains("u.country"));
        assertTrue(sql.contains("u.city"));
    }

    // ========== JoiningQuery 简单测试 ==========

    @Test
    public void testSimpleJoiningQuery() {
        JoiningQuery query = new JoiningQuery();
        assertNotNull(query);
        assertNotNull(query.toString());
    }

    @Test
    public void testJoiningQueryWithWhere() {
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqual(col("u.status"), "active");

        JoiningQuery query = new JoiningQuery().where(where);
        assertNotNull(query);
    }

    @Test
    public void testJoiningQueryWithLimit() {
        JoiningQuery query = new JoiningQuery().limit(0, 10);
        assertNotNull(query);
    }

    @Test
    public void testJoiningQueryWithDistinct() {
        JoiningQuery query = new JoiningQuery().distinct();
        assertNotNull(query);
    }

    @Test
    public void testJoiningQueryWithOrderBy() {
        JoiningQuery query = new JoiningQuery().orderBy(new JoiningOrderByAsc(col("u.name")));
        assertNotNull(query);
    }

    @Test
    public void testJoiningQueryWithInclude() {
        JoiningQuery query = new JoiningQuery().include(new JoiningIncludeMultipleColumn(col("u.id"), col("u.name")));
        assertNotNull(query);
    }

    // ========== JoiningQuery 复杂测试 ==========

    @Test
    public void testComplexJoiningQueryAllComponents() {
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqual(col("u.status"), "active");
        where.andGreaterThan(col("o.amount"), 100));

        JoiningQuery query = new JoiningQuery()
                .where(where)
                .include(new JoiningIncludeMultipleColumn(col("u.id"), col("u.name"), col("o.amount")))
                .orderBy(new JoiningOrderByDesc(col("o.created_at")))
                .limit(10, 20)
                .distinct();

        assertNotNull(query);
        assertNotNull(query.toString());
    }

    @Test
    public void testComplexJoiningQueryWithMultipleOrderBy() {
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqual(col("u.status"), "active");
        where.andIn(col("u.country"), Arrays.asList("US", "CA", "UK"));

        JoiningOrderByMultipleColumn orderBy = new JoiningOrderByMultipleColumn(
                new JoiningOrderByAsc(col("u.country")),
                new JoiningOrderByDesc(col("o.created_at"))
        );

        JoiningQuery query = new JoiningQuery()
                .where(where)
                .include(new JoiningIncludeMultipleColumn(col("u.id"), col("u.name"), col("o.amount")))
                .orderBy(orderBy)
                .limit(0, 50);

        assertNotNull(query);
    }

    // ========== JoiningGroupByQuery 测试 ==========

    @Test
    public void testSimpleJoiningGroupByQuery() {
        JoiningQuery baseQuery = new JoiningQuery()
                .where(new JoiningQueryWhere().andEqual(col("u.status"), "active"));

        JoiningGroupByQuery groupByQuery = baseQuery.groupBy(new JoiningGroupBySingleColumn(col("u.country")));
        assertNotNull(groupByQuery);
    }

    @Test
    public void testComplexJoiningGroupByQuery() {
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqual(col("u.status"), "active");
        where.andBetween(col("o.amount"), 100, 1000);
        where.andNotNull(col("u.country"));

        JoiningQuery baseQuery = new JoiningQuery()
                .where(where)
                .include(new JoiningIncludeMultipleColumn(col("u.country"), col("COUNT(*) as total")));

        JoiningGroupByQuery groupByQuery = baseQuery.groupBy(new JoiningGroupBySingleColumn(col("u.country")));
        assertNotNull(groupByQuery);
    }

    @Test
    public void testJoiningGroupByQueryMultipleColumns() {
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqual(col("u.status"), "active");

        JoiningQuery baseQuery = new JoiningQuery()
                .where(where)
                .include(new JoiningIncludeMultipleColumn(
                        col("u.country"), col("u.city"), col("COUNT(*) as cnt")));

        JoiningGroupByQuery groupByQuery = baseQuery.groupBy(new JoiningGroupByMultipleColumn(col("u.country"), col("u.city")));
        assertNotNull(groupByQuery);
    }

    // ========== JoiningQueryWhere 复杂组合条件 ==========

    @Test
    public void testJoiningQueryWithNestedWhere() {
        JoiningQueryWhere subWhere = new JoiningQueryWhere();
        subWhere.orEqual(col("o.status"), "paid");
        subWhere.orEqual(col("o.status"), "shipped");

        JoiningQueryWhere mainWhere = new JoiningQueryWhere();
        mainWhere.andEqual(col("u.status"), "active");
        mainWhere.put(subWhere);

        JoiningQuery query = new JoiningQuery()
                .where(mainWhere)
                .include(new JoiningIncludeMultipleColumn(col("u.id"), col("u.name"), col("COUNT(o.id)"));

        assertNotNull(query);
    }

    @Test
    public void testJoiningQueryWithInCondition() {
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andIn(col("u.country"), Arrays.asList("US", "CA", "UK", "DE"));
        where.andBetween(col("o.amount"), 50, 500);

        JoiningQuery query = new JoiningQuery()
                .where(where)
                .orderBy(new JoiningOrderByDesc(col("o.created_at")))
                .limit(0, 100);

        assertNotNull(query);
    }

    // ========== JoiningQuery 多种 Include 组件组合验证 ==========

    @Test
    public void testJoiningIncludeVariety() {
        JoiningIncludeBy single = new JoiningIncludeSingleColumn(col("u.id"));
        JoiningIncludeBy multi = new JoiningIncludeMultipleColumn(col("u.id"), col("u.name"));

        assertNotNull(single.toSqlClause());
        assertNotNull(multi.toSqlClause());
    }

    // ========== JoiningQuery 复杂 GroupBy ==========

    @Test
    public void testJoiningGroupByMultipleColumnList() {
        JoiningGroupBy groupBy = new JoiningGroupByMultipleColumn(Arrays.asList(col("u.country"), col("u.city"), col("u.year")));
        String sql = groupBy.toSqlClause();
        assertNotNull(sql);
    }

    // ========== JoiningQuery toString 验证 ==========

    @Test
    public void testJoiningQueryToString() {
        JoiningQuery query = new JoiningQuery()
                .where(new JoiningQueryWhere().andEqual(col("u.status"), "active"))
                .limit(0, 20);
        String str = query.toString();
        assertNotNull(str);
    }

    @Test
    public void testJoiningGroupByQueryToString() {
        JoiningQuery baseQuery = new JoiningQuery()
                .where(new JoiningQueryWhere().andEqual(col("u.status"), "active"));

        JoiningGroupByQuery groupByQuery = baseQuery.groupBy(new JoiningGroupBySingleColumn(col("u.country")));
        assertNotNull(groupByQuery.toString());
    }
}
