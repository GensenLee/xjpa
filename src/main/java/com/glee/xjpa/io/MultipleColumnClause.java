package com.glee.xjpa.io;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.constant.XJpaConstant;
import com.glee.xjpa.io.column.TableColumn;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2026/3/23
 * @description 包括列
 */
public class MultipleColumnClause extends SqlClause {

    private final List<TableColumn> columns;

    public MultipleColumnClause(List<TableColumn> columns) {
        this.columns = columns;
    }

    public MultipleColumnClause(TableColumn... columns) {
        this.columns = Arrays.asList(columns);
    }


    @Override
    public String toSqlClause() {
        return columns.stream().map(TableColumn::getColumnLabel)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining(XJpaConstant.COMMA_MARK));
    }
}
