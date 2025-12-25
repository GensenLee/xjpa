package com.glee.xjpa.sql.executor.result.reader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/7
 * @description 行影响结果
 */
public class AffectRowResult implements Result {

    private final int affectRow;

    public AffectRowResult(int affectRow) {
        this.affectRow = affectRow;
    }

    @Override
    public int getAffectRow() {
        return affectRow;
    }

    @Override
    public List<Map<String, Object>> getRawResults() {
        return Collections.emptyList();
    }
}
