package com.glee.xjpa.io;

import com.glee.xjpa.io.groupby.GroupBy;
import com.glee.xjpa.io.groupby.GroupByMultipleColumn;
import com.glee.xjpa.io.groupby.GroupBySingleColumn;
import com.glee.xjpa.io.include.IncludeBy;
import com.glee.xjpa.io.include.IncludeCountingColumn;
import com.glee.xjpa.io.include.IncludeMultipleColumn;
import com.glee.xjpa.io.include.IncludeSingleColumn;
import com.glee.xjpa.io.orderby.*;
import com.glee.xjpa.sql.where.usermodel.QueryWhere;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Query / IncludeBy / OrderBy / GroupBy 综合测试
 * 覆盖简单和复杂的查询描述对象构建
 */
public class QueryClauseTest {

    // ========== IncludeBy 简单测试 ==========

    @Test
    public void testSingleColumnInclude() {
        IncludeBy include = new IncludeSingleColumn("id");
        assertEquals("id", include.toSqlClause());
    }

    @Test
    public void testMultipleColumnIncludeVarargs() {
        IncludeBy include = new IncludeMultipleColumn("id", "name", "email");
        String sql = include.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.contains("id"));
        assertTrue(sql.contains("name"));
        assertTrue(sql.contains("email"));
    }

    @Test
    public void testMultipleColumnIncludeList() {
        IncludeBy include = new IncludeMultipleColumn(Arrays.asList("id", "name", "email"));
        String sql = include.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.contains("id"));
        assertTrue(sql.contains("name"));
        assertTrue(sql.contains("email"));
    }

    // ========== IncludeBy 复杂测试 ==========

    @Test
    public void testCountingColumnInclude() {
        // SELECT COUNT(*)
        IncludeBy include = new IncludeCountingColumn();
        String sql = include.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.toLowerCase().contains("count"));
    }

    @Test
    public void testIncludeWithAliasedColumn() {
        IncludeBy include = new IncludeSingleColumn("u.id");
        assertEquals("u.id", include.toSqlClause());
    }

    // ========== OrderBy 简单测试 ==========

    @Test
    public void testSimpleAscOrder() {
        OrderBy orderBy = new OrderByAsc("name");
        assertEquals("name asc", orderBy.toSqlClause());
    }

    @Test
    public void testSimpleDescOrder() {
        OrderBy orderBy = new OrderByDesc("created_at");
        assertEquals("created_at desc", orderBy.toSqlClause());
    }

    // ========== OrderBy 多列排序 ==========

    @Test
    public void testMultipleColumnOrderBy() {
        OrderBy orderBy = new OrderByMultipleColumn(
                new OrderByAsc("status"),
                new OrderByDesc("created_at")
        );
        String sql = orderBy.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.contains("status asc"));
        assertTrue(sql.contains("created_at desc"));
    }

    @Test
    public void testMultipleColumnOrderByWithList() {
        OrderBy orderBy = new OrderByMultipleColumn(Arrays.asList(
                new OrderByAsc("status"),
                new OrderByAsc("name"),
                new OrderByDesc("created_at")
        ));
        String sql = orderBy.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.contains("status asc"));
        assertTrue(sql.contains("name asc"));
        assertTrue(sql.contains("created_at desc"));
    }

    // ========== GroupBy 简单测试 ==========

    @Test
    public void testSingleColumnGroupBy() {
        GroupBy groupBy = new GroupBySingleColumn("status");
        assertEquals("status", groupBy.toSqlClause());
    }

    @Test
    public void testMultipleColumnGroupBy() {
        GroupBy groupBy = new GroupByMultipleColumn("status", "country");
        String sql = groupBy.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.contains("status"));
        assertTrue(sql.contains("country"));
    }

    // ========== GroupBy 复杂测试 ==========

    @Test
    public void testGroupByMultipleColumnWithList() {
        GroupBy groupBy = new GroupByMultipleColumn(Arrays.asList("status", "country", "year"));
        String sql = groupBy.toSqlClause();
        assertNotNull(sql);
        assertTrue(sql.contains("status"));
        assertTrue(sql.contains("country"));
        assertTrue(sql.contains("year"));
    }

    // ========== Query 简单测试 ==========

    @Test
    public void testSimpleQuery() {
        Query query = new Query();
        assertNotNull(query);
        assertNotNull(query.toString());
    }

    @Test
    public void testQueryWithWhere() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "active");

        Query query = new Query().where(where);
        assertNotNull(query);
    }

    @Test
    public void testQueryWithLimit() {
        Query query = new Query().limit(0, 10);
        assertNotNull(query);
    }

    @Test
    public void testQueryWithDistinct() {
        Query query = new Query().distinct();
        assertNotNull(query);
    }

    @Test
    public void testQueryWithOrderBy() {
        Query query = new Query().orderBy(new OrderByAsc("name"));
        assertNotNull(query);
    }

    @Test
    public void testQueryWithInclude() {
        Query query = new Query().include(new IncludeMultipleColumn("id", "name"));
        assertNotNull(query);
    }

    // ========== Query 复杂测试 ==========

    @Test
    public void testComplexQueryAllComponents() {
        // WHERE status = 'active' AND age > 18
        // ORDER BY created_at DESC
        // LIMIT 10, 20
        // SELECT id, name, email
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "active");
        where.andGreaterThan("age", 18);

        Query query = new Query()
                .where(where)
                .include(new IncludeMultipleColumn("id", "name", "email"))
                .orderBy(new OrderByDesc("created_at"))
                .limit(10, 20)
                .distinct();

        assertNotNull(query);
        assertNotNull(query.toString());
    }

    @Test
    public void testComplexQueryWithMultipleOrderBy() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "active");
        where.andIn("country", Arrays.asList("US", "CA", "UK"));

        OrderByMultipleColumn orderBy = new OrderByMultipleColumn(
                new OrderByAsc("country"),
                new OrderByDesc("created_at")
        );

        Query query = new Query()
                .where(where)
                .include(new IncludeMultipleColumn("id", "name", "email", "created_at"))
                .orderBy(orderBy)
                .limit(0, 50);

        assertNotNull(query);
    }

    // ========== GroupByInQuery 测试 ==========

    @Test
    public void testSimpleGroupByInQuery() {
        Query baseQuery = new Query()
                .where(new QueryWhere().andEqual("status", "active"));

        GroupByInQuery groupByQuery = baseQuery.groupBy(new GroupBySingleColumn("country"));
        assertNotNull(groupByQuery);
    }

    @Test
    public void testComplexGroupByInQuery() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "active");
        where.andBetween("age", 18, 65);
        where.andNotNull("country");

        Query baseQuery = new Query()
                .where(where)
                .include(new IncludeMultipleColumn("country", "COUNT(*) as total"))
                .orderBy(new OrderByDesc("total"));

        GroupByInQuery groupByQuery = baseQuery.groupBy(new GroupBySingleColumn("country"));
        assertNotNull(groupByQuery);
    }

    @Test
    public void testGroupByInQueryMultipleColumns() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "active");

        Query baseQuery = new Query()
                .where(where)
                .include(new IncludeMultipleColumn("country", "city", "COUNT(*) as cnt"));

        GroupByInQuery groupByQuery = baseQuery.groupBy(new GroupByMultipleColumn("country", "city"));
        assertNotNull(groupByQuery);
    }

    // ========== 条件构建 add(boolean, ...) 模式 ==========

    @Test
    public void testConditionalIncludeBuilding() {
        boolean includeEmail = true;
        boolean includePhone = false;

        if (includeEmail) {
            IncludeBy include = new IncludeMultipleColumn("id", "name", "email");
            assertEquals(3, include.toSqlClause().split(",").length);
        }

        if (includePhone) {
            IncludeBy include = new IncludeMultipleColumn("id", "name", "email", "phone");
            assertEquals(4, include.toSqlClause().split(",").length);
        }
    }

    // ========== 各种 Include 类型的混合验证 ==========

    @Test
    public void testIncludeVariety() {
        IncludeBy single = new IncludeSingleColumn("id");
        IncludeBy multi = new IncludeMultipleColumn("id", "name");
        IncludeBy counting = new IncludeCountingColumn();

        assertNotNull(single.toSqlClause());
        assertNotNull(multi.toSqlClause());
        assertNotNull(counting.toSqlClause());

        assertTrue(counting.toSqlClause().toLowerCase().contains("count"));
    }

    // ========== OrderBy 链 / 表达式验证 ==========

    @Test
    public void testOrderByMultipleColumnsFormat() {
        OrderBy orderBy = new OrderByMultipleColumn(
                new OrderByAsc("status"),
                new OrderByDesc("created_at"),
                new OrderByAsc("name")
        );
        String sql = orderBy.toSqlClause();
        // 验证有逗号分隔
        assertTrue(sql.contains(", "));
    }

    // ========== Query 的 toString 验证 ==========

    @Test
    public void testQueryToString() {
        Query query = new Query()
                .where(new QueryWhere().andEqual("status", "active"))
                .limit(0, 20);
        String str = query.toString();
        assertNotNull(str);
        // 至少有 Query 前缀
        assertTrue(str.contains("Query"));
    }

    // ========== GroupByInQuery 的 toString ==========

    @Test
    public void testGroupByInQueryToString() {
        Query baseQuery = new Query()
                .where(new QueryWhere().andEqual("status", "active"));

        GroupByInQuery groupByQuery = baseQuery.groupBy(new GroupBySingleColumn("country"));
        assertNotNull(groupByQuery.toString());
    }
}
