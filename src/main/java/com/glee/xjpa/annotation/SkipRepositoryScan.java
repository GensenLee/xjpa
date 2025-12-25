package com.glee.xjpa.annotation;

import java.lang.annotation.*;

/**
 * @author GENSEN
 * @date 2022/11/15
 * @description 跳过repository注册扫描
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipRepositoryScan {
}
