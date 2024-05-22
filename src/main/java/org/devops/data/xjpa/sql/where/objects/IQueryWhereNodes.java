package org.devops.data.xjpa.sql.where.objects;

import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/22
 * @description 多节点
 */
public interface IQueryWhereNodes extends IQueryWhereObject {

    List<IQueryWhereObject> children();

    @Override
    default void accept(IQueryWhereObjectVisitor visitor) {
        if (visitor == null) {
            return;
        }
        visitor.visit(this);
    }

    /**
     * @param whereObject
     * @return
     */
    boolean contains(IQueryWhereObject whereObject);
}
