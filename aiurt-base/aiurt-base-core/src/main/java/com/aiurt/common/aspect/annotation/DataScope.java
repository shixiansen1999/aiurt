package com.aiurt.common.aspect.annotation;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 * @author wgp
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    /**
     * 部门表的别名
     */
    public String deptAlias() default "";

    /**
     * 用户表的别名
     */
    public String userAlias() default "";

    /**
     * 线路表的别名
     */
    public String lineAlias() default "";

    /**
     * 站点的别名
     */
    public String stationAlias() default "";

    /**
     * 专业表的别名
     */
    public String majorAlias() default "";

    /**
     * 站点的别名
     */
    public String subsystemAlias() default "";
}
