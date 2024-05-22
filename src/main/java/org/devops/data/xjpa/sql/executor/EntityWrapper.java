package org.devops.data.xjpa.sql.executor;

/**
 * @author GENSEN
 * @date 2022/11/10
 * @description entity对象包装
 */
public class EntityWrapper<V> {

    private final V entity;

    private final int index;

    public EntityWrapper(V entity, int index) {
        this.entity = entity;
        this.index = index;
    }

    /**
     * @return
     */
    public int getIndex() {
        return index;
    }

    public V getEntity() {
        return entity;
    }
}
