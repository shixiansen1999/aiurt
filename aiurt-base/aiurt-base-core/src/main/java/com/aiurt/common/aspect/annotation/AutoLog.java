package com.aiurt.common.aspect.annotation;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.ModuleType;
import org.springframework.context.annotation.Primary;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @Author scott
 * @email jeecgos@163.com
 * @Date 2019年1月14日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoLog {

	/**
	 * 日志内容，按钮名称（操作描述）
	 *
	 * @return
	 */
	String value() default "";

	/**
	 * 日志类型
	 *
	 * @return 0:操作日志;1:登录日志;2:定时任务;
	 */
	int logType() default CommonConstant.LOG_TYPE_2;

	/**
	 * 操作日志类型
	 *
	 * @return （1查询，2添加，3修改，4删除, 5导入，6导出）
	 */
	int operateType() default 0;

	/**
	 * 操作日志类型，比如：修改-更新状态
	 * @return
	 */
	String operateTypeAlias() default "";

	/**
	 * 模块类型 默认为common
	 * @return
	 */
	ModuleType module() default ModuleType.COMMON;

	/**
	 * 菜单路径
	 * @return
	 */
	String permissionUrl() default "";


}
