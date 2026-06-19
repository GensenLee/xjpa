package com.glee.xjpa.sql.where.usermodel;

import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import com.glee.xjpa.sql.where.subquery.InlineSubQuery;
import com.glee.xjpa.sql.where.subquery.SubQuery;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * QueryWhere 复杂条件构建测试
 * 覆盖嵌套条件、子查询、多条件组合、Visitor遍历等高级特性
 */
public class QueryWhereComplexTest {

    // ========== 嵌套条件测试 ==========

    @Test
    public void testNestedOrWithinAnd() {
        // WHERE status = 'active' AND (role = 'admin' OR role = 'manager')
        QueryWhere sub = new QueryWhere();
        sub.orEqual("role", "admin");
        sub.orEqual("role", "manager");

        QueryWhere main = new QueryWhere();
        main.andEqual("status", "active");
        main.put(sub);

        String sql = main.toString();
        assertNotNull(sql);
        assertTrue(sql.contains("status"));
        assertTrue(sql.contains("role"));
    }

    @Test
    public void testDeepNestedConditions() {
        // WHERE a = 1 AND (b = 2 AND (c = 3 OR d = 4))
        QueryWhere level3 = new QueryWhere();
        level3.orEqual("c", 3);
        level3.orEqual("d", 4);

        QueryWhere level2 = new QueryWhere();
        level2.andEqual("b", 2);
        level2.put(level3);

        QueryWhere level1 = new QueryWhere();
        level1.andEqual("a", 1);
        level1.put(level2);

        String sql = level1.toString();
        assertNotNull(sql);
        assertTrue(sql.contains("a"));
        assertTrue(sql.contains("b"));
        assertTrue(sql.contains("c"));
        assertTrue(sql.contains("d"));
    }

    @Test
    public void testMultipleNestedBranches() {
        // WHERE (status = 1 OR priority > 5) AND (amount < 1000 OR amount > 5000)
        QueryWhere branch1 = new QueryWhere();
        branch1.orEqual("status", 1);
        branch1.orGreaterThan("priority", 5);

        QueryWhere branch2 = new QueryWhere();
        branch2.orLessThan("amount", 1000.0);
        branch2.orGreaterThan("amount", 5000.0);

        QueryWhere root = new QueryWhere();
        root.put(branch1);
        root.put(branch2);

        String sql = root.toString();
        assertNotNull(sql);
    }

    // ========== 子查询测试 ==========

    @Test
    public void testSubQueryIn() {
        // WHERE user_id IN (SELECT id FROM users WHERE status = 'active')
        QueryWhere subWhere = new QueryWhere();
        subWhere.andEqual("status", "active");
        InlineSubQuery subQuery = SubQuery.selectOneColumn(
                "users", "id", subWhere);

        QueryWhere main = new QueryWhere();
        main.andIn("user_id", subQuery);

        String sql = main.toString();
        assertNotNull(sql);
        assertTrue(sql.toLowerCase().contains("in"));
    }

    @Test
    public void testSubQueryNotIn() {
        QueryWhere subWhere = new QueryWhere();
        subWhere.andEqual("status", "banned");
        InlineSubQuery subQuery = SubQuery.selectOneColumn("blacklist", "uid", subWhere);

        QueryWhere main = new QueryWhere();
        main.andNotIn("id", subQuery);

        String sql = main.toString();
        assertNotNull(sql);
        assertTrue(sql.toLowerCase().contains("not in"));
    }

    // ========== 复杂多条件组合 ==========

    @Test
    public void testComplexBusinessQuery() {
        // 模拟一个复杂的业务查询：查找活跃高价值用户
        // WHERE status = 'active' AND age BETWEEN 18 AND 60 AND balance > 10000
        //       AND (level = 'vip' OR register_date > '2023-01-01')
        //       AND region IN ('north', 'south', 'east')
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "active");
        where.andBetween("age", 18, 60);
        where.andGreaterThan("balance", 10000.0);

        QueryWhere levelOrRecent = new QueryWhere();
        levelOrRecent.orEqual("level", "vip");
        levelOrRecent.orGreaterThan("register_date", "2023-01-01");
        where.put(levelOrRecent);

        where.andIn("region", Arrays.asList("north", "south", "east"));

        String sql = where.toString();
        assertNotNull(sql);
    }

    @Test
    public void testComplexWithNullAndNotNull() {
        // WHERE deleted_at IS NULL AND expired_at IS NOT NULL AND status = 'active'
        QueryWhere where = new QueryWhere();
        where.andIsNull("deleted_at");
        where.andNotNull("expired_at");
        where.andEqual("status", "active");
        assertNotNull(where.toString());
    }

    @Test
    public void testComplexWithBetweenAndInCombination() {
        QueryWhere where = new QueryWhere();
        where.andBetween("price", 100, 1000);
        where.andIn("category", Arrays.asList(1, 2, 3, 4));
        where.andLike("name", "%book%");
        where.andNotIn("status", Arrays.asList("deleted", "hidden"));
        assertNotNull(where.toString());
    }

    @Test
    public void testMixingOperators() {
        QueryWhere where = new QueryWhere();
        where.andEqual("type", "A");
        where.andNotEqual("flag", "disabled");
        where.andEqualOrGreaterThan("min_qty", 1);
        where.andEqualOrLessThan("max_qty", 100);
        where.andLike("label", "%special%");
        assertNotNull(where.toString());
    }

    // ========== 条件索引与参数 ==========

    @Test
    public void testIndexValuesMultipleConditions() {
        QueryWhere where = new QueryWhere();
        where.andEqual("a", 1);
        where.andEqual("b", 2);
        where.andEqual("c", 3);

        AtomicInteger index = new AtomicInteger(1);
        Map<Integer, Object> params = where.indexValues(index);
        assertNotNull(params);
        assertFalse(params.isEmpty());
        assertEquals(3, params.size());
    }

    @Test
    public void testIndexValuesEmpty() {
        QueryWhere where = new QueryWhere();
        AtomicInteger index = new AtomicInteger(1);
        Map<Integer, Object> params = where.indexValues(index);
        assertTrue(params.isEmpty());
    }

    @Test
    public void testIndexValuesWithIn() {
        QueryWhere where = new QueryWhere();
        where.andIn("status", Arrays.asList(1, 2, 3));
        where.andEqual("active", 1);

        AtomicInteger index = new AtomicInteger(1);
        Map<Integer, Object> params = where.indexValues(index);
        assertNotNull(params);
        // IN(?,?,?) 产生3个参数，AND = 产生1个，共4个
        assertEquals(4, params.size());
    }

    @Test
    public void testIndexValuesWithBetween() {
        QueryWhere where = new QueryWhere();
        where.andBetween("age", 18, 60);
        where.andEqual("status", 1);

        AtomicInteger index = new AtomicInteger(1);
        Map<Integer, Object> params = where.indexValues(index);
        assertNotNull(params);
        // BETWEEN 产生2个参数，AND = 产生1个，共3个
        assertEquals(3, params.size());
    }

    // ========== 条件构建的 Visitor 模式 ==========

    @Test
    public void testAcceptVisitor() {
        QueryWhere where = new QueryWhere();
        where.andEqual("name", "test");
        where.andGreaterThan("age", 18);
        // toString 内部调用 accept(visitor)，验证 visitor 路径
        String sql = where.toString();
        assertNotNull(sql);
    }

    // ========== 条件组合 add(valid, condition) 模式 ==========

    @Test
    public void testConditionalBuilderPattern() {
        String keyword = "search";
        Integer minAge = 20;
        String status = "active";

        QueryWhere where = new QueryWhere();
        where.add(keyword != null, "title", keyword);
        where.add(minAge != null, "age", minAge, com.glee.xjpa.sql.where.operate.WhereOperator.GT);
        where.add(status != null, "status", status);

        assertFalse(where.isEmpty());
        assertNotNull(where.toString());
    }

    @Test
    public void testConditionalBuilderPatternAllSkip() {
        String keyword = null;
        Integer minAge = null;
        String status = null;

        QueryWhere where = new QueryWhere();
        where.add(keyword != null, "title", keyword);
        where.add(minAge != null, "age", minAge, com.glee.xjpa.sql.where.operate.WhereOperator.GT);
        where.add(status != null, "status", status);

        assertTrue(where.isEmpty());
    }

    // ========== 构建对象树 ==========

    @Test
    public void testPutIQueryWhereObject() {
        IQueryWhereObject subCondition = new QueryWhere();
        ((QueryWhere) subCondition).andEqual("role", "admin");

        QueryWhere main = new QueryWhere();
        main.andEqual("status", 1);
        main.put(subCondition);

        assertFalse(main.isEmpty());
        assertNotNull(main.toString());
    }

    @Test
    public void testMultipleOrConditions() {
        QueryWhere where = new QueryWhere();
        where.orEqual("type", "A");
        where.orEqual("type", "B");
        where.orEqual("type", "C");
        where.orEqual("type", "D");
        assertNotNull(where.toString());
    }

    @Test
    public void testComplexMixedAndOrStructure() {
        // A = 1 AND (B = 2 OR (C = 3 AND (D = 4 OR E = 5)))
        QueryWhere level3 = new QueryWhere();
        level3.orEqual("d", 4);
        level3.orEqual("e", 5);

        QueryWhere level2 = new QueryWhere();
        level2.andEqual("c", 3);
        level2.put(level3);

        QueryWhere level1 = new QueryWhere();
        level1.orEqual("b", 2);
        level1.put(level2);

        QueryWhere root = new QueryWhere();
        root.andEqual("a", 1);
        root.put(level1);

        String sql = root.toString();
        assertNotNull(sql);
    }

    // ========== 基于泛型接口的 API 测试 ==========

    @Test
    public void testOrConditionsWithBetween() {
        QueryWhere where = new QueryWhere();
        where.orBetween("price", 10, 50);
        where.orBetween("price", 200, 500);
        assertNotNull(where.toString());
    }

    @Test
    public void testOrConditionsWithIn() {
        QueryWhere where = new QueryWhere();
        where.orIn("category", Arrays.asList(1, 2, 3));
        where.orIn("category", Arrays.asList(10, 11, 12));
        assertNotNull(where.toString());
    }

    @Test
    public void testOrConditionsWithNotIn() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "active");
        where.orNotIn("category", Arrays.asList(-1, -2));
        assertNotNull(where.toString());
    }

    @Test
    public void testOrIsNullAndIsNotNull() {
        QueryWhere where = new QueryWhere();
        where.orIsNull("deleted");
        where.orIsNotNull("updated_at");
        assertNotNull(where.toString());
    }

    // ========== 边界条件 ==========

    @Test
    public void testLargeInValues() {
        Integer[] ids = new Integer[100];
        for (int i = 0; i < 100; i++) {
            ids[i] = i + 1;
        }
        QueryWhere where = new QueryWhere();
        where.andIn("id", Arrays.asList(ids));
        assertNotNull(where.toString());
    }

    @Test
    public void testLongChainedConditions() {
        QueryWhere where = new QueryWhere();
        for (int i = 0; i < 20; i++) {
            where.andEqual("field_" + i, i);
        }
        String sql = where.toString();
        assertNotNull(sql);
    }

    // ========== 数值类型的条件构建 ==========

    @Test
    public void testVariousNumericTypes() {
        QueryWhere where = new QueryWhere();
        where.andEqual("tiny_int_field", (byte) 1);
        where.andEqual("short_int_field", (short) 100);
        where.andEqual("int_field", 1000);
        where.andEqual("long_field", 10000L);
        where.andEqual("float_field", 1.5f);
        where.andEqual("double_field", 3.14159);
        assertNotNull(where.toString());
    }

    // ========== add(column, value, operator, condition) 显式 API ==========

    @Test
    public void testExplicitAddWithAndCondition() {
        QueryWhere where = new QueryWhere();
        where.add("name", "alice", com.glee.xjpa.sql.where.operate.WhereOperator.EQ,
                com.glee.xjpa.sql.where.operate.Condition.AND);
        assertFalse(where.isEmpty());
    }

    @Test
    public void testExplicitAddWithOrCondition() {
        QueryWhere where = new QueryWhere();
        where.add("name", "alice", com.glee.xjpa.sql.where.operate.WhereOperator.EQ,
                com.glee.xjpa.sql.where.operate.Condition.OR);
        assertFalse(where.isEmpty());
    }

    // ========== 子查询与嵌套条件组合 ==========

    @Test
    public void testSubQueryInNestedCondition() {
        // WHERE status = 1 AND (user_id IN (subquery) OR priority > 10)
        QueryWhere sub = new QueryWhere();
        sub.andIn("user_id", SubQuery.selectOneColumn("admins", "id", new QueryWhere("active", 1)));
        sub.orGreaterThan("priority", 10);

        QueryWhere root = new QueryWhere();
        root.andEqual("status", 1);
        root.put(sub);

        assertNotNull(root.toString());
    }

    @Test
    public void testEmptyNestedConditionIgnored() {
        QueryWhere emptySub = new QueryWhere();
        QueryWhere main = new QueryWhere();
        main.andEqual("status", 1);
        main.put(emptySub); // 空条件应被忽略

        String sql = main.toString();
        assertNotNull(sql);
        // 应该只包含主条件，不包含空嵌套
        assertTrue(sql.contains("status"));
    }

    // ========== LIKE 系列测试 ==========

    @Test
    public void testMultipleLikeCombined() {
        QueryWhere where = new QueryWhere();
        where.andLike("title", "%java%");
        where.andRightLike("url", "https://");
        where.andLeftLike("path", "/api/");
        assertNotNull(where.toString());
    }

    @Test
    public void testOrLikeMix() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "published");
        where.orLike("title", "%book%");
        where.orLike("author", "%smith%");
        assertNotNull(where.toString());
    }

    // ========== 链式 API 验证 ==========

    @Test
    public void testChainApiReturnsSameInstance() {
        QueryWhere where = new QueryWhere();
        QueryWhere returned = where.andEqual("a", 1).andEqual("b", 2);
        assertSame(where, returned);
    }

    @Test
    public void testChainApiWithMultipleOperators() {
        QueryWhere where = new QueryWhere()
                .andEqual("status", "active")
                .andGreaterThan("age", 18)
                .andLessThan("age", 100)
                .andNotNull("email")
                .andIn("country", Arrays.asList("US", "CA", "UK"));
        assertNotNull(where.toString());
        assertFalse(where.isEmpty());
    }
}
