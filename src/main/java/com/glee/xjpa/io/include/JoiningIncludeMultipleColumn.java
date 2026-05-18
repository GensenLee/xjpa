package com.glee.xjpa.io.include;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.constant.XJpaConstant;
import com.glee.xjpa.io.column.TableColumn;

import java.util.Arrays;
import java.util.List;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接查询多列
 */
public class JoiningIncludeMultipleColumn extends JoiningIncludeBy {

    private final List<TableColumn> columns;

    public JoiningIncludeMultipleColumn(List<TableColumn> columns) {
        this.columns = columns;
    }

    public JoiningIncludeMultipleColumn(TableColumn... columns) {
        this.columns = Arrays.asList(columns);
    }

    @Override
    public String toSqlClause() {
        return StrUtil.join(XJpaConstant.COMMA_MARK, columns.stream().map(TableColumn::getColumnLabel).toArray());
    }
}
