package com.glee.xjpa.io.groupby;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.constant.XJpaConstant;
import com.glee.xjpa.io.column.TableColumn;

import java.util.Arrays;
import java.util.List;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接查询多列分组
 */
public class JoiningGroupByMultipleColumn extends JoiningGroupBy {

    private final List<TableColumn> columns;

    public JoiningGroupByMultipleColumn(List<TableColumn> columns) {
        this.columns = columns;
    }

    public JoiningGroupByMultipleColumn(TableColumn... columns) {
        this.columns = Arrays.asList(columns);
    }

    @Override
    public String toSqlClause() {
        return StrUtil.join(XJpaConstant.COMMA_MARK, columns.stream().map(TableColumn::getColumnLabel).toArray());
    }
}
