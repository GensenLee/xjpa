package org.devops.data.xjpa.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author GENSEN
 * @date 2023/1/4
 * @description 表数据
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableSetting {

    /**
     * @return 表ddl，用于建表
     */
    String ddl() default "";

    String ddlPath() default "";

    /**
     * @return 是否强制关闭该表的
     */
    boolean disableSoftDelete() default false;

    @Deprecated
    @AliasFor("disableSoftDelete")
    boolean forceCloseSoftDelete() default false;


}
