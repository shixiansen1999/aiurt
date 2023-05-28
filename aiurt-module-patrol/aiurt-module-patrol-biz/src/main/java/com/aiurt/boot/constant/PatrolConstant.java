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
     * 巡检计划-巡检频次：一天1次
     */
    Integer PLAN_PERIOD_ONE = 1;
    /**
     * 巡检计划-巡检频次：一周1次
     */
    Integer PLAN_PERIOD_TWO = 2;
    /**
     * 巡检计划-巡检频次：一周2次
     */
    Integer PLAN_PERIOD_THREE = 3;
    /**
     * 巡检计划-巡检频次：一月1次
     */
    Integer PLAN_PERIOD_FOUR = 4;
    /**
     * 巡检计划-巡检频次：一月2次
     */
    Integer PLAN_PERIOD_FIVE = 5;
    /**
     * 巡检计划-巡检频次：两天1次
     */
    Integer PLAN_PERIOD_TWO_DAY = 6;
    /**
     * 巡检计划-巡检频次：三天1次
     */
    Integer PLAN_PERIOD_THREE_DAY = 7;
    /**
     * 巡检计划-巡检频次：三月1次
     */
    Integer PLAN_PERIOD_THREE_MONTH = 8;
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
     * 巡检任务状态(未漏检)
     */
    Integer UNOMIT_STATUS = 0;
    /**
     * 巡检任务状态(已漏检)
     */
    Integer OMIT_STATUS = 1;
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
     * 巡检策略季巡
     */
    Integer STRATEGY_QUARTER = 3;
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
    /**
     * 巡检计划生成任务状态-已生成
     */
    Integer PLAN_CREATED = 1;

    /**
     * 巡检检修项是巡检项目
     */
    Integer IS_CHECK_RESULT = 1;
    /**
     * 巡检检查结果异常状态
     */
    Integer NOT_CHECK_RESULT = 0;
    /**
     * 巡检检查结果异常状态
     */
    String MANAGER = "admin";
    /**
     * 数据填写类型：1无
     */
    Integer INPUT_TYPE_1 = 1;
    /**
     * 工单-数据填写类型：选择型
     */
    Integer DEVICE_INP_TYPE = 2;
    /**
     * 工单-数据填写类型：输入型
     */
    Integer DEVICE_OUT = 3;
    /**
     * 巡视项检查值是否必填：0否
     */
    Integer REQUIRED_0 = 0;
    /**
     * 巡视项检查值是否必填：1是
     */
    Integer REQUIRED_1 = 0;
    /**
     * 是否与设备类型相关：是
     */
    String IS_DEVICE_TYPE = "是";
    /**
     * 是否与设备类型相关：否
     */
    String IS_NOT_DEVICE_TYPE = "否";
    /**
     * 是否与设备类型相关：生效
     */
    String ACTIVE = "生效";
    /**
     * 是否与设备类型相关：未生效
     */
    String NOT_ACTIVE = "未生效";
    /**
     * 层级：一级
     */
    String ONE_LEVEL = "一级";
    /**
     * 层级：子级
     */
    String SON_LEVEL = "子级";
    /**
     * 数据填写类型：1无
     */
    String DATE_TYPE_NO = "无";
    /**
     * 工单-数据填写类型：选择型
     */
    String DATE_TYPE_OT = "选择项";
    /**
     * 工单-数据填写类型：输入型
     */
    String DATE_TYPE_IP= "输入项";
    /**
     * 巡检配置项-无父级
     */
    String NO_PARENT= "无";
    /**
     * 巡视抽查状态：0未确认
     */
    Integer SPOT_CHECK_STATUS_0 = 0;
}
