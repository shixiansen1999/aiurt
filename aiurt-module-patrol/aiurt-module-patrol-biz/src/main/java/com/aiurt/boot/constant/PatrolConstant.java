package com.aiurt.boot.constant;

/**
 * 巡检模块常量
 *
 * @author CJB
 */
public interface PatrolConstant {

    /**
     * 巡检计划生效状态
     */
    Integer PLAN_STATUS_ENABLE = 1;
    /**
     * 巡检任务初始状态(待指派）
     */
    Integer TASK_INIT = 0;
    /**
     * 巡检任务状态(待确认）
     */
    Integer TASK_CONFIRM = 1;
    /**
     * 巡检任务状态(待执行）
     */
    Integer TASK_EXECUTE = 2;
    /**
     * 巡检任务状态(已退回）
     */
    Integer TASK_RETURNED = 3;
    /**
     * 巡检任务状态(执行中）
     */
    Integer TASK_RUNNING = 4;
    /**
     * 巡检任务状态(已驳回)
     */
    Integer TASK_BACK = 5;
    /**
     * 巡检任务状态(待审核)
     */
    Integer TASK_AUDIT = 6;
    /**
     * 巡检任务状态(已完成)
     */
    Integer TASK_COMPLETE = 7;
    /**
     * 巡检任务状态(已漏检)
     */
    Integer TASK_MISSED = 8;
    /**
     * 巡检任务异常状态
     */
    Integer TASK_ABNORMAL = 0;
    /**
     * 巡检任务正常状态
     */
    Integer TASK_UNABNORMAL = 1;
    /**
     * 巡检任务未作废状态
     */
    Integer TASK_UNDISCARD = 0;
    /**
     * 巡检任务已作废状态
     */
    Integer TASK_DISCARD = 1;
    /**
     * 巡检任务未处置状态
     */
    Integer TASK_UNDISPOSE = 0;
    /**
     * 巡检任务已处置状态
     */
    Integer TASK_DISPOSE = 1;
    /**
     * 巡检审核通过状态
     */
    Integer AUDIT_PASS = 1;
    /**
     * 巡检任务要审核
     */
    Integer TASK_CHECK = 1;
    /**
     * 巡检任务不要审核
     */
    Integer TASK_NOT_CHECK = 0;
    /**
     * 巡检审核不通过状态
     */
    Integer AUDIT_NOPASS = 0;
    /**
     * 巡检任务-未重新生成任务状态
     */
    Integer TASK_UNREBUILD = 0;
    /**
     * 巡检任务-已重新生成任务状态
     */
    Integer TASK_REBUILD = 1;
    /**
     * 巡检任务来源-常规指派
     */
    Integer TASK_COMMON = 2;
    /**
     * 巡检任务来源-手工下发
     */
    Integer TASK_MANUAL = 3;
    /**
     * 巡检策略日巡
     */
    Integer STRATEGY_DAY = 0;
    /**
     * 巡检策略周巡
     */
    Integer STRATEGY_WEEK = 1;
    /**
     * 巡检策略月巡
     */
    Integer STRATEGY_MONTH = 2;
    /**
     * 巡检检查结果正常状态
     */
    Integer RESULT_NORMAL = 1;
    /**
     * 巡检检查结果异常状态
     */
    Integer RESULT_EXCEPTION = 0;
    /**
     * 与设备类型无关
     */
    Integer DEVICE_INDEPENDENCE = 0;
    /**
     * 巡检工单初始状态-未开始
     */
    Integer BILL_INIT = 0;
    /**
     * 巡检工单初始状态-进行中
     */
    Integer BILL_PROCESSING = 1;
    /**
     * 巡检工单初始状态-已完成
     */
    Integer BILL_COMPLETE = 2;
}
