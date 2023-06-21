package com.aiurt.boot.annotation;

import java.lang.annotation.*;

/**
 * 实体标识ID的注解,在作为主键的实体字段上添加
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ElasticId {
}
