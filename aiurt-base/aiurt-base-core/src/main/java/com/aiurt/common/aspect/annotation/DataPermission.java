package com.aiurt.common.aspect.annotation;

import java.lang.annotation.*;

/**
 * 数据权限组
 *
 * @author MrWei
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    DataColumn[] value();

}
