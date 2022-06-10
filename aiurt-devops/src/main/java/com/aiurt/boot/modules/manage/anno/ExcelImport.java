package com.aiurt.boot.modules.manage.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 导入时获取字段对应的excel的顺序及调用对应的验证方法进行校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExcelImport {
    /**
     * excel表格中对应字段的位置
     */
    int index();

    /**
     * 格式验证的类
     */
    String clz() default "com.swsc.support.modules.manage.anno.DefaulValidate";

    /**
     * 格式验证的方法名
     */
    String method() default "";

    /**
     * 格式验证失败的返回信息
     */
    String message() default "";

    /**
     * 时间格式
     */
    String pattern() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 数据转换方法
     */
    String changeMethod() default "";

}
