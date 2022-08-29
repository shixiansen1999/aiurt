package com.aiurt.common.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/8/2917:52
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LimitSubmit {
    String key() ;
    /**
     * 默认 10s
     */
    int limit() default 10;

    /**
     * 请求完成后 是否一直等待
     * true则等待
     * @return
     */
    boolean needAllWait() default true;
}
