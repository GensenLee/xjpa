package com.glee.xjpa.io.orderby;

import com.glee.xjpa.constant.XJpaConstant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2026/3/25
 * @description 多列排序
 */
public class OrderByMultipleColumn extends OrderBy {

    private final List<OrderBy> orderByList;

    public OrderByMultipleColumn(List<OrderBy> orderByList) {
        this.orderByList = orderByList;
    }

    public OrderByMultipleColumn(OrderBy... orderBys) {
        this.orderByList = Arrays.asList(orderBys);
    }


    @Override
    public String toSqlClause() {
        return orderByList.stream()
                .map(OrderBy::toSqlClause)
                .collect(Collectors.joining(XJpaConstant.COMMA_MARK));
    }
}
