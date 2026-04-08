package com.glee.xjpa.io.groupby;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.constant.XJpaConstant;

import java.util.Arrays;
import java.util.List;

/**
 * @author GENSEN
 * @date 2026/3/25
 * @description 多列
 */
public class GroupByMultipleColumn extends GroupBy {

    private final List<String> columns;

    public GroupByMultipleColumn(List<String> columns) {
        this.columns = columns;
    }

    public GroupByMultipleColumn(String... columns) {
        this.columns = Arrays.asList(columns);
    }

    @Override
    public String toSqlClause() {
        return StrUtil.join(XJpaConstant.COMMA_MARK, columns);
    }
}
