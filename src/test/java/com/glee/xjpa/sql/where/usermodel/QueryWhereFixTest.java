package com.glee.xjpa.sql.where.usermodel;

import com.glee.xjpa.sql.where.operate.WhereOperator;
import com.glee.xjpa.sql.where.subquery.InlineSubQuery;
import com.glee.xjpa.sql.where.subquery.SubQuery;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * 修复测试：验证 QueryWhere 对 SubQuery.selectOneColumn 的正确调用
 * 以及使用泛型接口的一些边界场景
 */
public class QueryWhereFixTest {

    /**
     * 模拟一个用户实体类，用于测试 SubQuery（第一个参数要求是 Class）
     */
    private static class UserEntity {
        Long id;
        String status;
    }

    private static class OrderEntity {
        Long id;
        Long userId;
        Integer status;
    }

    @Test
    public void testSubQueryUsingClassParam() {
        // SELECT id FROM users WHERE status = 'active'
        QueryWhere subWhere = new QueryWhere();
        subWhere.andEqual("status", "active");
        InlineSubQuery subQuery = SubQuery.selectOneColumn(UserEntity.class, "id", subWhere);

        QueryWhere main = new QueryWhere();
        main.andIn("user_id", subQuery);
        assertNotNull(main.toString());
        assertTrue(main.toString().toLowerCase().contains("in"));
    }

    @Test
    public void testSubQueryNotIn() {
        QueryWhere subWhere = new QueryWhere();
        subWhere.andEqual("status", "banned");
        InlineSubQuery subQuery = SubQuery.selectOneColumn(UserEntity.class, "id", subWhere);

        QueryWhere main = new QueryWhere();
        main.andEqual("active", 1);
        main.orNotIn("id", subQuery);
        assertNotNull(main.toString());
    }

    @Test
    public void testSubQueryOrIn() {
        QueryWhere subWhere = new QueryWhere();
        subWhere.andEqual("flag", 1);
        InlineSubQuery subQuery = SubQuery.selectOneColumn(OrderEntity.class, "user_id", subWhere);

        QueryWhere main = new QueryWhere();
        main.orIn("id", subQuery);
        assertNotNull(main.toString());
    }

    @Test
    public void testSubQueryWithoutWhere() {
        InlineSubQuery subQuery = SubQuery.selectOneColumn(UserEntity.class, "id");

        QueryWhere main = new QueryWhere();
        main.andEqual("status", "active");
        main.andIn("id", subQuery);
        assertNotNull(main.toString());
    }

    @Test
    public void testMultipleSubQueriesCombined() {
        QueryWhere activeUsersWhere = new QueryWhere();
        activeUsersWhere.andEqual("status", "active");
        InlineSubQuery activeUsersSub = SubQuery.selectOneColumn(UserEntity.class, "id", activeUsersWhere);

        QueryWhere premiumOrdersWhere = new QueryWhere();
        premiumOrdersWhere.andEqual("level", "premium");
        InlineSubQuery premiumOrdersSub = SubQuery.selectOneColumn(OrderEntity.class, "user_id", premiumOrdersWhere);

        QueryWhere main = new QueryWhere();
        main.andIn("user_id", activeUsersSub);
        main.andNotIn("blocked_id", premiumOrdersSub);
        assertNotNull(main.toString());
    }

    @Test
    public void testSubQueryInlineSql() {
        QueryWhere subWhere = new QueryWhere();
        subWhere.andEqual("status", "active");
        InlineSubQuery subQuery = SubQuery.selectOneColumn(UserEntity.class, "id", subWhere);
        // 测试子查询的 SQL 生成
        String inlineSql = subQuery.inlineSql(true);
        assertNotNull(inlineSql);
    }

    @Test
    public void testSubQueryWithNestedWhere() {
        QueryWhere nestedSub = new QueryWhere();
        nestedSub.andEqual("role", "admin");
        nestedSub.orEqual("role", "superadmin");

        QueryWhere outerSub = new QueryWhere();
        outerSub.andEqual("active", 1);
        outerSub.put(nestedSub);

        InlineSubQuery subQuery = SubQuery.selectOneColumn(UserEntity.class, "id", outerSub);

        QueryWhere main = new QueryWhere();
        main.andIn("owner_id", subQuery);
        main.andGreaterThan("amount", 100);
        assertNotNull(main.toString());
    }

    @Test
    public void testWhereOperatorCoverage() {
        // 覆盖 WhereOperator 的所有常用枚举值
        QueryWhere where = new QueryWhere();
        where.add("f1", 1, WhereOperator.EQ, com.glee.xjpa.sql.where.operate.Condition.AND);
        where.add("f2", 1, WhereOperator.NEQ, com.glee.xjpa.sql.where.operate.Condition.AND);
        where.add("f3", 1, WhereOperator.GT, com.glee.xjpa.sql.where.operate.Condition.AND);
        where.add("f4", 1, WhereOperator.GE, com.glee.xjpa.sql.where.operate.Condition.AND);
        where.add("f5", 1, WhereOperator.LT, com.glee.xjpa.sql.where.operate.Condition.AND);
        where.add("f6", 1, WhereOperator.LE, com.glee.xjpa.sql.where.operate.Condition.AND);
        where.add("f7", "%x%", WhereOperator.LIKE, com.glee.xjpa.sql.where.operate.Condition.AND);
        // IN 单独测试
        where.andIn("category", Arrays.asList("A", "B", "C"));
        where.andBetween("price", 10, 100);
        where.andIsNull("deleted_at");
        where.andNotNull("created_at");
        assertNotNull(where.toString());
        assertFalse(where.isEmpty());
    }
}
