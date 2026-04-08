package com.glee.xjpa.annotation;

import java.lang.annotation.*;

/**
 * @author GENSEN
 * @date 2023/1/4
 * @description 表数据
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XJpaTable {

    /**
     * @return 表ddl，用于建表
     */
    String ddl() default "";

    String ddlPath() default "";

}
