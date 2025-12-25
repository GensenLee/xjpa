package com.glee.xjpa.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description xjpa 数据源配置，注解于dataSource bean上
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XjpaDataSource {

    /**
     * @return 指定给repository所在的包
     */
    String[] forPackages();

}
