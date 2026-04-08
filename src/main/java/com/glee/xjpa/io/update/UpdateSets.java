package com.glee.xjpa.io.update;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GENSEN
 * @date 2026/3/31
 * @description 更新集
 */
public class UpdateSets {

    private final List<UpdateSetClause> setList = new ArrayList<>();

    public UpdateSets() {
    }

    public UpdateSets append(String column, UpdateSetBlock value){
        setList.add(new DefaultUpdateSetClause(column, value));
        return this;
    }

    public UpdateSets append(String column, Object value){
        setList.add(new DefaultUpdateSetClause(column, new DefaultUpdateSetBlock(value)));
        return this;
    }

    List<UpdateSetClause> getUpdateFields() {
        return setList;
    }
}
