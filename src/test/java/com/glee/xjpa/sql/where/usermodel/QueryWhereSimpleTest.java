package com.glee.xjpa.sql.where.usermodel;

import com.glee.xjpa.sql.where.operate.Condition;
import com.glee.xjpa.sql.where.operate.WhereOperator;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * QueryWhere 简单条件构建测试
 * 覆盖所有单条件的基础用法
 */
public class QueryWhereSimpleTest {

    @Test
    public void testEmptyWhere() {
        QueryWhere where = new QueryWhere();
        assertTrue(where.isEmpty());
    }

    @Test
    public void testAndEqual() {
        QueryWhere where = new QueryWhere();
        where.andEqual("name", "test");
        assertFalse(where.isEmpty());
        String sql = where.toString();
        assertTrue(sql.contains("name"));
        assertTrue(sql.contains("="));
    }

    @Test
    public void testAndNotEqual() {
        QueryWhere where = new QueryWhere();
        where.andNotEqual("status", "deleted");
        String sql = where.toString();
        assertTrue(sql.contains("status"));
        assertTrue(sql.contains("<>"));
    }

    @Test
    public void testAndGreaterThan() {
        QueryWhere where = new QueryWhere();
        where.andGreaterThan("age", 18);
        String sql = where.toString();
        assertTrue(sql.contains("age"));
        assertTrue(sql.contains(">"));
    }

    @Test
    public void testAndLessThan() {
        QueryWhere where = new QueryWhere();
        where.andLessThan("score", 100);
        String sql = where.toString();
        assertTrue(sql.contains("score"));
        assertTrue(sql.contains("<"));
    }

    @Test
    public void testAndEqualOrGreaterThan() {
        QueryWhere where = new QueryWhere();
        where.andEqualOrGreaterThan("create_time", "2024-01-01");
        String sql = where.toString();
        assertTrue(sql.contains(">="));
    }

    @Test
    public void testAndEqualOrLessThan() {
        QueryWhere where = new QueryWhere();
        where.andEqualOrLessThan("amount", 1000.0);
        String sql = where.toString();
        assertTrue(sql.contains("<="));
    }

    @Test
    public void testAndLike() {
        QueryWhere where = new QueryWhere();
        where.andLike("title", "%java%");
        String sql = where.toString();
        assertTrue(sql.contains("like"));
    }

    @Test
    public void testAndLeftLike() {
        QueryWhere where = new QueryWhere();
        where.andLeftLike("content", "test");
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("like"));
    }

    @Test
    public void testAndRightLike() {
        QueryWhere where = new QueryWhere();
        where.andRightLike("url", "http");
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("like"));
    }

    @Test
    public void testAndIn() {
        QueryWhere where = new QueryWhere();
        where.andIn("status", new HashSet<>(Arrays.asList(1, 2, 3)));
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("in"));
        assertTrue(sql.contains("("));
        assertTrue(sql.contains(")"));
    }

    @Test
    public void testAndInWithList() {
        QueryWhere where = new QueryWhere();
        where.andIn("category", Arrays.asList("A", "B", "C"));
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("in"));
    }

    @Test
    public void testAndNotIn() {
        QueryWhere where = new QueryWhere();
        where.andNotIn("id", Arrays.asList(1L, 2L, 3L));
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("not in"));
    }

    @Test
    public void testAndBetween() {
        QueryWhere where = new QueryWhere();
        where.andBetween("age", 18, 60);
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("between"));
        assertTrue(sql.toLowerCase().contains("and"));
    }

    @Test
    public void testAndNotBetween() {
        QueryWhere where = new QueryWhere();
        where.andNotBetween("price", 100, 200);
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("not between"));
    }

    @Test
    public void testAndIsNull() {
        QueryWhere where = new QueryWhere();
        where.andIsNull("deleted_at");
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("is null"));
    }

    @Test
    public void testAndIsNotNull() {
        QueryWhere where = new QueryWhere();
        where.andNotNull("updated_at");
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("is not null"));
    }

    @Test
    public void testOrEqual() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", 1).orEqual("status", 2);
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("or"));
    }

    @Test
    public void testOrGreaterThan() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "active");
        where.orGreaterThan("priority", 10);
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("or"));
    }

    @Test
    public void testCustomCondition() {
        QueryWhere where = new QueryWhere();
        where.equal("name", "test", Condition.AND);
        assertNotNull(where.toString());
    }

    @Test
    public void testGenericAddWithBoolean() {
        String name = "test";
        QueryWhere where = new QueryWhere();
        where.add(name != null, "name", name);
        assertFalse(where.isEmpty());
    }

    @Test
    public void testGenericAddWithBooleanSkip() {
        String name = null;
        QueryWhere where = new QueryWhere();
        where.add(name != null, "name", name);
        assertTrue(where.isEmpty());
    }

    @Test
    public void testGenericAddWithOperator() {
        QueryWhere where = new QueryWhere();
        where.add(true, "status", WhereOperator.IS_NOT_NULL);
        assertFalse(where.isEmpty());
    }

    @Test
    public void testGenericAddWithValueAndOperator() {
        QueryWhere where = new QueryWhere();
        where.add(true, "age", 18, WhereOperator.GT);
        assertFalse(where.isEmpty());
    }

    @Test
    public void testConditionSwitch() {
        QueryWhere where = new QueryWhere();
        where.condition(Condition.OR);
        where.andEqual("status", 1);
        where.andEqual("deleted", 0);
        assertNotNull(where.toString());
    }

    @Test
    public void testConstructorWithColumnAndValue() {
        QueryWhere where = new QueryWhere("id", 1L);
        assertFalse(where.isEmpty());
        assertTrue(where.toString().contains("id"));
    }

    @Test
    public void testConstructorWithOperator() {
        QueryWhere where = new QueryWhere("age", 18, WhereOperator.GT);
        assertFalse(where.isEmpty());
        assertTrue(where.toString().contains(">"));
    }

    @Test
    public void testConstructorWithCondition() {
        QueryWhere where = new QueryWhere("name", "test", WhereOperator.EQ, Condition.AND);
        assertFalse(where.isEmpty());
    }

    @Test
    public void testGenericAndOperator() {
        QueryWhere where = new QueryWhere();
        where.and("status", WhereOperator.IS_NOT_NULL);
        assertFalse(where.isEmpty());
    }

    @Test
    public void testGenericAndWithValueAndOperator() {
        QueryWhere where = new QueryWhere();
        where.and("age", 18, WhereOperator.GT);
        assertFalse(where.isEmpty());
    }

    @Test
    public void testGenericOrOperator() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", 1);
        where.or("deleted_at", WhereOperator.IS_NULL);
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("or"));
    }

    @Test
    public void testMultipleSimpleConditions() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "active");
        where.andGreaterThan("age", 18);
        where.andLessThan("age", 100);
        String sql = where.toString();
        assertTrue(sql.toLowerCase().contains("and"));
    }

    @Test
    public void testToStringOnEmpty() {
        QueryWhere where = new QueryWhere();
        String result = where.toString();
        assertNotNull(result);
    }
}
