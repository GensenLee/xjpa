package com.glee.xjpa.io.include;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.constant.XJpaConstant;

import java.util.Arrays;
import java.util.List;

/**
 * @author GENSEN
 * @date 2026/3/25
 * @description 多列
 */
public class IncludeMultipleColumn extends IncludeBy {

    private final List<String> columns;

    public IncludeMultipleColumn(List<String> columns) {
        this.columns = columns;
    }

    public IncludeMultipleColumn(String... columns) {
        this.columns = Arrays.asList(columns);
    }

    @Override
    public String toSqlClause() {
        return StrUtil.join(XJpaConstant.COMMA_MARK, columns);
    }
}
