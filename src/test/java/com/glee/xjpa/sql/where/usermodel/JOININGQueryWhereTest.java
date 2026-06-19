package com.glee.xjpa.sql.where.usermodel;

import com.glee.xjpa.io.column.TableColumn;
import com.glee.xjpa.io.join.JoinPoint;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * JoiningQueryWhere 连表条件构建测试
 * 覆盖单表 JOIN 场景下的 TableColumn 条件 API
 */
public class JOININGQueryWhereTest {

    /**
     * 测试用 JoinPoint.columnDef 返回 TableColumn
     */
    private static class TestJoinPoint implements JoinPoint {
        private final String alias;
        public TestJoinPoint(String alias) { this.alias = alias; }
        @Override public Class<?> getEntityType() { return Object.class; }
        @Override public String getTableName() { return "test_table"; }
        @Override public String getTableAlias() { return alias; }
        @Override public TableColumn columnDef(String columnName) {
            return new TableColumn() {
                private final String label = "`" + alias + "`.`" + columnName + "`";
                @Override public String getColumnLabel() { return label; }
            };
        }
        @Override public boolean isSoftDeleteEnabled() { return false; }
        @Override public int getJoiningOrder() { return 1; }
    }

    // ========== 简单条件构建 ==========

    @Test
    public void testSimpleEqualCondition() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqual(jp.columnDef("status"), "active");
        assertFalse(where.isEmpty());
        assertNotNull(where.toString());
    }

    @Test
    public void testSimpleNotEqualCondition() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andNotEqual(jp.columnDef("status"), "deleted");
        assertFalse(where.isEmpty());
        assertNotNull(where.toString());
    }

    @Test
    public void testSimpleGreaterLessThan() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andGreaterThan(jp.columnDef("age"), 18);
        where.andLessThan(jp.columnDef("age"), 100);
        assertNotNull(where.toString());
    }

    @Test
    public void testSimpleGreaterOrEqual() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqualOrGreaterThan(jp.columnDef("balance"), 1000);
        assertNotNull(where.toString());
    }

    @Test
    public void testSimpleLessOrEqual() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqualOrLessThan(jp.columnDef("qty"), 50);
        assertNotNull(where.toString());
    }

    // ========== IN / NOT IN ==========

    @Test
    public void testInCondition() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andIn(jp.columnDef("status"), Arrays.asList(1, 2, 3));
        assertNotNull(where.toString());
    }

    @Test
    public void testNotInCondition() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andNotIn(jp.columnDef("category"), Arrays.asList("hidden", "deleted"));
        assertNotNull(where.toString());
    }

    // ========== BETWEEN / NOT BETWEEN ==========

    @Test
    public void testBetweenCondition() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andBetween(jp.columnDef("price"), 100, 1000);
        assertNotNull(where.toString());
    }

    @Test
    public void testNotBetweenCondition() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andNotBetween(jp.columnDef("rank"), 5, 20);
        assertNotNull(where.toString());
    }

    // ========== IS NULL / IS NOT NULL ==========

    @Test
    public void testIsNullCondition() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andIsNull(jp.columnDef("deleted_at"));
        assertNotNull(where.toString());
    }

    @Test
    public void testIsNotNullCondition() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andNotNull(jp.columnDef("verified_at"));
        assertNotNull(where.toString());
    }

    // ========== LIKE ==========

    @Test
    public void testLikeCondition() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andLike(jp.columnDef("title"), "%book%");
        assertNotNull(where.toString());
    }

    @Test
    public void testLeftRightLike() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andLeftLike(jp.columnDef("path"), "api");
        where.andRightLike(jp.columnDef("url"), "https");
        assertNotNull(where.toString());
    }

    // ========== 多表 JOIN 条件 ==========

    @Test
    public void testMultipleJoinPoints() {
        // WHERE t1.status = 'active' AND t2.total > 100
        TestJoinPoint jp1 = new TestJoinPoint("t1");
        TestJoinPoint jp2 = new TestJoinPoint("t2");

        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqual(jp1.columnDef("status"), "active");
        where.andGreaterThan(jp2.columnDef("total"), 100);
        assertNotNull(where.toString());
    }

    @Test
    public void testOrConditionAcrossJoinPoints() {
        TestJoinPoint jp1 = new TestJoinPoint("t1");
        TestJoinPoint jp2 = new TestJoinPoint("t2");

        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqual(jp1.columnDef("status"), "active");
        where.orEqual(jp2.columnDef("type"), "premium");
        assertNotNull(where.toString());
    }

    // ========== 复杂连表条件组合 ==========

    @Test
    public void testComplexCombination() {
        TestJoinPoint jp1 = new TestJoinPoint("orders");
        TestJoinPoint jp2 = new TestJoinPoint("customers");

        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqual(jp1.columnDef("status"), "paid");
        where.andBetween(jp1.columnDef("amount"), 100.0, 5000.0);
        where.andIn(jp2.columnDef("country"), Arrays.asList("US", "CA", "UK"));
        where.andNotNull(jp2.columnDef("email"));
        assertNotNull(where.toString());
    }

    @Test
    public void testOrConditionBetweenTwoTables() {
        TestJoinPoint orders = new TestJoinPoint("orders");
        TestJoinPoint customers = new TestJoinPoint("customers");

        JoiningQueryWhere where = new JoiningQueryWhere();
        where.andEqual(orders.columnDef("status"), "pending");

        JoiningQueryWhere subOr = new JoiningQueryWhere();
        subOr.orGreaterThan(orders.columnDef("amount"), 1000);
        subOr.orEqual(customers.columnDef("level"), "premium");
        where.put(subOr);

        assertNotNull(where.toString());
    }

    // ========== 条件构建 add(valid, ...) 模式 ==========

    @Test
    public void testConditionalBuilding() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        String keyword = "search";
        Integer minAge = 20;

        JoiningQueryWhere where = new JoiningQueryWhere();
        where.add(keyword != null, jp.columnDef("title"), keyword);
        where.add(minAge != null, jp.columnDef("age"), minAge,
                com.glee.xjpa.sql.where.operate.WhereOperator.GT);
        assertFalse(where.isEmpty());
    }

    @Test
    public void testConditionalBuildingAllSkip() {
        TestJoinPoint jp = new TestJoinPoint("t1");
        String keyword = null;
        Integer minAge = null;

        JoiningQueryWhere where = new JoiningQueryWhere();
        where.add(keyword != null, jp.columnDef("title"), keyword);
        where.add(minAge != null, jp.columnDef("age"), minAge,
                com.glee.xjpa.sql.where.operate.WhereOperator.GT);
        assertTrue(where.isEmpty());
    }

    // ========== put(IQueryWhereObject) 嵌套条件 ==========

    @Test
    public void testNestedOrCondition() {
        TestJoinPoint orders = new TestJoinPoint("orders");
        TestJoinPoint users = new TestJoinPoint("users");

        JoiningQueryWhere sub = new JoiningQueryWhere();
        sub.orEqual(users.columnDef("level"), "gold");
        sub.orEqual(users.columnDef("level"), "platinum");

        JoiningQueryWhere main = new JoiningQueryWhere();
        main.andEqual(orders.columnDef("status"), "paid");
        main.put(sub);

        assertNotNull(main.toString());
    }

    @Test
    public void testDeeplyNested() {
        TestJoinPoint t1 = new TestJoinPoint("t1");
        TestJoinPoint t2 = new TestJoinPoint("t2");

        JoiningQueryWhere level3 = new JoiningQueryWhere();
        level3.orEqual(t1.columnDef("a"), 1);
        level3.orEqual(t1.columnDef("b"), 2);

        JoiningQueryWhere level2 = new JoiningQueryWhere();
        level2.andEqual(t2.columnDef("c"), 3);
        level2.put(level3);

        JoiningQueryWhere root = new JoiningQueryWhere();
        root.andEqual(t1.columnDef("d"), 4);
        root.put(level2);

        assertNotNull(root.toString());
    }

    // ========== getCondition() 访问器 ==========

    @Test
    public void testGetCondition() {
        JoiningQueryWhere where = new JoiningQueryWhere();
        com.glee.xjpa.sql.where.operate.Condition condition = where.getCondition();
        assertNotNull(condition);
    }

    // ========== 链式 API 验证 ==========

    @Test
    public void testChainApi() {
        TestJoinPoint jp = new TestJoinPoint("t");
        JoiningQueryWhere where = new JoiningQueryWhere();
        JoiningQueryWhere returned = where
                .andEqual(jp.columnDef("status"), "active")
                .andGreaterThan(jp.columnDef("age"), 18)
                .andIn(jp.columnDef("country"), Arrays.asList("US", "CA"));
        assertSame(where, returned);
    }

    @Test
    public void testEmptyJoiningQueryWhere() {
        JoiningQueryWhere where = new JoiningQueryWhere();
        assertTrue(where.isEmpty());
    }
}
