package com.aiurt.common.aspect.annotation;

import java.lang.annotation.*;

/**
 * 主要用于标记数据权限中基于lineCode进行过滤的字段。
 *
 * @author wgp
 * @date 2022-07-17
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LineFilterColumn {
}
