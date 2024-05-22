package org.devops.data.xjpa.repository.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author GENSEN
 * @date 2022/12/6
 * @description 标记执行完需要丢失过程数据的方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@interface DisposeAfterReturn {
}
