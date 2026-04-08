package com.glee.xjpa.annotation;

import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

/**
 * @author GENSEN
 * @date 2023/1/4
 * @description Repository类
 */
@Repository
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XJpaRepository {

    /**
     * @return 该表指定使用的数据源对象bean名称
     */
    String dataSource() default "";
}
