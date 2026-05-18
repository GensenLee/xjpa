package com.glee.xjpa.io.column;

import com.glee.xjpa.constant.XJpaConstant;
import com.glee.xjpa.io.join.JoinPoint;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接点表字段
 */
public class JoinPointTableColumn implements TableColumn, Serializable {

    private final JoinPoint joinPoint;

    private final String column;

    public JoinPointTableColumn(JoinPoint joinPoint, String column) {
        this.joinPoint = joinPoint;
        this.column = column;
    }

    @Override
    public String getColumnLabel() {
        return "`" + joinPoint.getTableAlias() + "`." + "`" + column + "`";
    }

    public JoinPoint getJoinPoint() {
        return joinPoint;
    }

    public String getColumn() {
        return column;
    }
}