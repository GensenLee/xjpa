package org.devops.data.xjpa.sql.where.objects;

import org.devops.data.xjpa.sql.where.operate.Condition;

/**
 * @author GENSEN
 * @date 2022/11/22
 * @description 连接器
 */
public interface IQueryWhereAttach {

    /**
     * @return 外接条件
     */
    Condition attachCondition();

    /**
     * @param condition 替换条件
     * @return
     */
    IQueryWhereAttach condition(Condition condition);

}
