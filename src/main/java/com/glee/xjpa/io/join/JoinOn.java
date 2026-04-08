package com.glee.xjpa.io.join;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description join on条件
 */
public interface JoinOn {


    /**
     * @param columnNameOnJoiningTable 当前正在连接的表的字段名
     * @param columnNameOnConnectedTable 被连接的表的字段名
     * 如：select * from user t1 left join order t2 on t2.user_id = t1.id
     * 即可以指定 columnNameOnJoiningTable为user_id，columnNameOnConnectedTable为id
     * XJpa会自动拼接上表别名在SQL中
     * @return
     */
    JoinOn on(String columnNameOnJoiningTable, String columnNameOnConnectedTable);

}
