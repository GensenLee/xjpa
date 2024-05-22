package org.devops.data.xjpa.join;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 连接类型
 */
public enum JoinType {
    LEFT_JOIN("left join"),
    RIGHT_JOIN("right join"),
    INNER_JOIN("inner join");

    final String grammar;

    JoinType(String grammar) {
        this.grammar = grammar;
    }
}
