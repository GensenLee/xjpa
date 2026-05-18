package com.glee.xjpa.io.having;

import com.glee.xjpa.sql.where.QueryWhereUtil;
import com.glee.xjpa.sql.where.usermodel.JoiningQueryWhere;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连接查询having条件
 */
public class JoiningWhereGroupByHaving extends JoiningGroupByHave {

    private final JoiningQueryWhere where;

    public JoiningWhereGroupByHaving(JoiningQueryWhere where) {
        this.where = where;
    }

    @Override
    public String toSqlClause() {
        return QueryWhereUtil.toWhereString(where, true);
    }
}
