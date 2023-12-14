package org.devops.data.xjpa.sql.where.objects;

import cn.hutool.core.util.StrUtil;
import org.devops.data.xjpa.sql.where.operate.Condition;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 遍历结果
 */
public class ObjectVisitResult {

    private final Condition condition;

    private final String whereString;

    public ObjectVisitResult(Condition condition, String whereString) {
        this.condition = condition;
        this.whereString = whereString;
    }

    public boolean isEmpty() {
        return StrUtil.isEmpty(whereString);
    }

    public static ObjectVisitResult empty() {
        return new ObjectVisitResult(null, null);
    }

}
