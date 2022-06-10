package com.aiurt.boot.modules.patrol.constant;

/**
 * @description: DeleteConstant
 * @author: Mr.zhao
 * @date: 2021/9/15 16:56
 */
public interface PatrolConstant {

	/**
	 * 删除状态
	 */
	Integer DEL_FLAG = 1;
	Integer UN_DEL_FLAG = 0;

	/**
	 * 开关状态
	 */
	Integer ENABLE = 1;
	Integer DISABLE = 0;


	/**
	 * 分隔符
	 */
	String SPL = ",";

	/**
	 * no字段小数点分隔符
	 */
	String NO_SPL = ".";

	/**
	 * 系统生成名称
	 */
	String ADMIN = "admin";

	/**
	 * db 名称
	 */
	String DB_PATROL_TASK_REPORT = "PatrolTaskReport";
	String DB_PATROL_TASK = "PatrolTask";
}
