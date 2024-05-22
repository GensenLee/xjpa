package org.devops.data.xjpa.repository;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2023/1/17
 * @description 查询
 */
public interface IStandardSelectRepository<K extends Serializable, V> extends ISelectRepository<K, V>, ISingleColumnSupportSelectRepository<K, V> {
}
