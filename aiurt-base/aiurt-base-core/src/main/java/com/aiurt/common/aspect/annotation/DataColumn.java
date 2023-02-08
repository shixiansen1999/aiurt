package com.aiurt.common.aspect.annotation;

import java.lang.annotation.*;

/**
 * 数据权限
 *
 * 一个注解只能对应一个模板
 * 占位符替换参考DataScopeType
 * @author MrWei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataColumn {

    /**
     * 占位符关键字
     */
    String[] key() default "deptName";

    /**
     * 占位符替换值
     */
    String[] value() default "org_code";

}
