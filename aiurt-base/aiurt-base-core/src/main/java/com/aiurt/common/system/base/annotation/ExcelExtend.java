package com.aiurt.common.system.base.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 导出基本注释扩展
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelExtend {

    /**
     * 批准
     * @return
     */
    public String remark() default "";

    /**
     * 是否必填
     */
    public boolean isRequired() default false;
}
