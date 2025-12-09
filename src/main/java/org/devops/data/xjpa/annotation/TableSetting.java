package org.devops.data.xjpa.annotation;

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
     * @return 是否开启逻辑删除
     */
    boolean logicDelete() default true;

}
