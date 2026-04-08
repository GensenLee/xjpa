package com.glee.xjpa.io.include;

import cn.hutool.core.util.StrUtil;

/**
 * @author GENSEN
 * @date 2026/3/25
 * @description 单列
 */
public class IncludeCountingColumn extends IncludeBy {

    private final String column;

    private String alias;

    public IncludeCountingColumn(String column) {
        this.column = column;
    }

    public IncludeCountingColumn(String column, String alias) {
        this.column = column;
        this.alias = alias;
    }

    @Override
    public String toSqlClause() {
        if (StrUtil.isBlank(alias)) {
            return "count(" + column + ")";
        }
        return "count(" + column + ") as " + alias;
    }
}
