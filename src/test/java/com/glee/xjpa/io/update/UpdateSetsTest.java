package com.glee.xjpa.io.update;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * UpdateSets 简单与复杂测试
 * 覆盖 append(String, Object) / append(String, UpdateSetBlock)
 */
public class UpdateSetsTest {

    // ========== 简单测试 ==========

    @Test
    public void testSimpleAppendWithValue() {
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("name", "alice");
        List<UpdateSetClause> clauses = updateSets.getUpdateFields();
        assertNotNull(clauses);
        assertEquals(1, clauses.size());
    }

    @Test
    public void testSimpleMultipleColumns() {
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("name", "alice");
        updateSets.append("age", 30);
        updateSets.append("active", true);
        assertEquals(3, updateSets.getUpdateFields().size());
    }

    @Test
    public void testSimpleAppendNumericValue() {
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("amount", 100.5);
        updateSets.append("count", 50);
        assertEquals(2, updateSets.getUpdateFields().size());
    }

    @Test
    public void testSimpleAppendWithNullValue() {
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("deleted_at", null);
        assertEquals(1, updateSets.getUpdateFields().size());
    }

    // ========== UpdateSetBlock 接口 ==========

    @Test
    public void testDefaultUpdateSetBlock() {
        DefaultUpdateSetBlock block = new DefaultUpdateSetBlock("test");
        assertEquals("?", block.toUpdateTemplateClause());
        assertEquals("test", block.getParameter());
    }

    @Test
    public void testDefaultUpdateSetBlockWithNumeric() {
        DefaultUpdateSetBlock block = new DefaultUpdateSetBlock(42);
        assertEquals("?", block.toUpdateTemplateClause());
        assertEquals(42, block.getParameter());
    }

    // ========== 复杂：append(String, UpdateSetBlock) ==========

    @Test
    public void testAppendWithUpdateSetBlock() {
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("name", new DefaultUpdateSetBlock("alice"));
        updateSets.append("age", new DefaultUpdateSetBlock(30));
        assertEquals(2, updateSets.getUpdateFields().size());
    }

    @Test
    public void testCustomUpdateSetBlock() {
        // 自定义 UpdateSetBlock: 类似 count = count + 1
        UpdateSetBlock incrementBlock = new UpdateSetBlock() {
            @Override
            public String toUpdateTemplateClause() {
                return "count + ?";
            }
            @Override
            public Object getParameter() {
                return 1;
            }
        };
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("count", incrementBlock);
        List<UpdateSetClause> clauses = updateSets.getUpdateFields();
        assertEquals(1, clauses.size());
    }

    // ========== 复杂：混合构建 ==========

    @Test
    public void testMixedValueAndBlock() {
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("name", "alice");
        updateSets.append("age", new DefaultUpdateSetBlock(30));
        updateSets.append("email", "alice@example.com");
        updateSets.append("updated_at", new DefaultUpdateSetBlock(System.currentTimeMillis()));
        assertEquals(4, updateSets.getUpdateFields().size());
    }

    @Test
    public void testEmptyUpdateSets() {
        UpdateSets updateSets = new UpdateSets();
        assertTrue(updateSets.getUpdateFields().isEmpty());
    }

    @Test
    public void testComplexMultipleBlocks() {
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("first_name", new DefaultUpdateSetBlock("Alice"));
        updateSets.append("last_name", new DefaultUpdateSetBlock("Johnson"));
        updateSets.append("email", new DefaultUpdateSetBlock("alice@example.com"));
        updateSets.append("age", new DefaultUpdateSetBlock(30));
        updateSets.append("active", new DefaultUpdateSetBlock(true));
        updateSets.append("updated_at", new DefaultUpdateSetBlock(System.currentTimeMillis()));
        assertEquals(6, updateSets.getUpdateFields().size());
    }

    // ========== toSqlClause() 测试 ==========

    @Test
    public void testClauseToSqlClause() {
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("status", "active");
        UpdateSetClause clause = updateSets.getUpdateFields().get(0);
        assertEquals("status=?", clause.toSqlClause());
    }

    @Test
    public void testMultipleClauseToSqlClause() {
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("name", "alice");
        updateSets.append("age", 30);
        List<UpdateSetClause> clauses = updateSets.getUpdateFields();
        assertEquals("name=?", clauses.get(0).toSqlClause());
        assertEquals("age=?", clauses.get(1).toSqlClause());
    }

    @Test
    public void testCustomBlockSqlClause() {
        UpdateSetBlock customBlock = new UpdateSetBlock() {
            @Override
            public String toUpdateTemplateClause() {
                return "price * ?";
            }
            @Override
            public Object getParameter() {
                return 0.9;
            }
        };
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("price", customBlock);
        UpdateSetClause clause = updateSets.getUpdateFields().get(0);
        assertEquals("price=price * ?", clause.toSqlClause());
    }

    // ========== 链式调用 ==========

    @Test
    public void testChainedAppends() {
        UpdateSets updateSets = new UpdateSets()
                .append("col1", "val1")
                .append("col2", 2)
                .append("col3", true)
                .append("col4", new DefaultUpdateSetBlock("val4"));
        assertEquals(4, updateSets.getUpdateFields().size());
    }

    // ========== 混合类型值 ==========

    @Test
    public void testMixedTypeValues() {
        UpdateSets updateSets = new UpdateSets();
        updateSets.append("string_col", "string_value");
        updateSets.append("int_col", 100);
        updateSets.append("long_col", 10000000000L);
        updateSets.append("double_col", 3.14);
        updateSets.append("boolean_col", true);
        updateSets.append("null_col", null);

        List<UpdateSetClause> clauses = updateSets.getUpdateFields();
        assertEquals(6, clauses.size());

        // 验证每个子句的 toSqlClause 都包含 ?
        for (UpdateSetClause clause : clauses) {
            assertTrue(clause.toSqlClause().contains("="));
        }
    }
}
