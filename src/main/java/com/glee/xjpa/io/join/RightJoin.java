package com.glee.xjpa.io.join;


import jakarta.persistence.criteria.JoinType;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description 右连接
 */
@SuppressWarnings("rawtypes")
public class RightJoin extends AbstractJoinOn {

    public RightJoin(Class rightTableEntityType) {
        super(rightTableEntityType, JoinType.RIGHT);
    }


}
