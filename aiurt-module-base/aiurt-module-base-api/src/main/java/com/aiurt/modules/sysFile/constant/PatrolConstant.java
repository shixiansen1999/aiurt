package com.aiurt.modules.sysFile.constant;

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
	 * db 名称
	 */
	String DB_PATROL_TASK_REPORT = "PatrolTaskReport";
	String DB_PATROL_TASK = "PatrolTask";
	/**
	 * 签名
	 */
	String SIGN= "Sign";

	/**
	 * 顶层父级
	 */
	Long NUM_LONG_0 = 0L;

	/**
	 * 巡检频率 1.一天1次 2.一周2次 3.一周1次 4.手动发放
	 */
	Integer PATROL_TACTICS_1 = 1;
	Integer PATROL_TACTICS_2 = 2;
	Integer PATROL_TACTICS_3 = 3;
	Integer PATROL_TACTICS_4 = 4;

	/**
	 * 报告状态: 文字项
	 */
	Integer REPORT_STATUS_0 = 0;
	/**
	 * 报告状态:正常
	 */
	Integer REPORT_STATUS_1 = 1;
	/**
	 * 报告状态:异常
	 */
	Integer REPORT_STATUS_2 = 2;
	/**
	 * 报告状态:已生成故障上报
	 */
	Integer REPORT_STATUS_3 = 3;

	/**
	 * 指派前缀
	 */
	String APPOINT_PREFIX = "appoint_pool_id:";
}
