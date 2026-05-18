package com.glee.xjpa.io.orderby;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.constant.XJpaConstant;

import java.util.Arrays;
import java.util.List;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接查询多列排序
 */
public class JoiningOrderByMultipleColumn extends JoiningOrderBy {

    private final List<JoiningOrderBy> orderByList;

    public JoiningOrderByMultipleColumn(List<JoiningOrderBy> orderByList) {
        this.orderByList = orderByList;
    }

    public JoiningOrderByMultipleColumn(JoiningOrderBy... orderBys) {
        this.orderByList = Arrays.asList(orderBys);
    }

    @Override
    public String toSqlClause() {
        return StrUtil.join(XJpaConstant.COMMA_MARK, orderByList.stream().map(JoiningOrderBy::toSqlClause).toArray());
    }
}
