package com.glee.xjpa.io;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.constant.XJpaConstant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2026/3/23
 * @description 多个字符串列
 */
public class MultiplePlainColumnClause extends SqlClause {

    private final List<String> columns;

    public MultiplePlainColumnClause(List<String> columns) {
        this.columns = columns;
    }

    public MultiplePlainColumnClause(String... columns) {
        this.columns = Arrays.asList(columns);
    }

    @Override
    public String toSqlClause() {
        return columns.stream().filter(StrUtil::isNotBlank).collect(Collectors.joining(XJpaConstant.COMMA_MARK));
    }

}
