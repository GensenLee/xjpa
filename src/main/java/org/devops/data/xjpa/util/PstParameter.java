package org.devops.data.xjpa.util;

import lombok.Getter;
import org.devops.data.xjpa.table.EntityTableField;

import javax.validation.constraints.NotNull;

/**
 * @author GENSEN
 * @date 2022/12/12
 * @description PreparedStatement参数
 */
@Getter
public class PstParameter {

    private final Object value;

    private final EntityTableField entityTableField;

    public PstParameter(Object value, @NotNull EntityTableField entityTableField) {
        this.value = value;
        this.entityTableField = entityTableField;
    }

    public boolean isNull() {
        return value == null;
    }

}
