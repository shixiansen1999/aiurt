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
     * 巡检任务初始状态(已指派）
     */
    Integer TASK_APPOINT = 1;
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
    Integer TASK_DISPOSE = 0;
}
