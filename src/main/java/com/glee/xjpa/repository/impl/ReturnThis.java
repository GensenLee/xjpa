package com.glee.xjpa.repository.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author GENSEN
 * @date 2022/12/6
 * @description 标记方法返回this
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@interface ReturnThis {
}
