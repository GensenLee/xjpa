package com.glee.xjpa.repository;

import java.io.Serializable;

/**
 * @author GENSEN
 * @date 2022/11/1
 * @description 增强curd
 */
public interface IEnhanceCurdRepository<K extends Serializable, V> extends ISelectRepository<K, V>, IInsertRepository<K, V>,
        IUpdateRepository<K, V>, IDeleteRepository<K, V>, IEnhanceRepository<K, V>, IStandardSelectRepository<K, V> {
}
