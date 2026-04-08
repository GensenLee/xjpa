package com.glee.xjpa.io.join;

import jakarta.persistence.criteria.JoinType;

/**
 * @author GENSEN
 * @date 2025/12/30
 * @description 左连接
 */
@SuppressWarnings("rawtypes")
public class LeftJoin extends AbstractJoinOn {

    public LeftJoin(Class rightTableEntityType) {
        super(rightTableEntityType, JoinType.LEFT);
    }

}
