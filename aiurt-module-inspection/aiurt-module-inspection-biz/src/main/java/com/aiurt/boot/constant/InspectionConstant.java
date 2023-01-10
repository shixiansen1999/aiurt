package com.aiurt.boot.constant;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/6/2215:14
 */
public class InspectionConstant {
    /**
     * 专业
     */
    public static final String MAJOR = "major";
    /**
     * 专业子系统
     */
    public static final String SUBSYSTEM = "subsystem";

    /**
     * 是否手工下发任务：否
     */
    public static final Integer NO_IS_MANUAL = 0;

    /**
     * 是否手工下发任务：是
     */
    public static final Integer IS_MANUAL = 1;

    /**
     * 检修周期类型：0周检
     */
    public static final String WEEKLY_INSPECTION = "0";
    /**
     * 检修任务状态：0待指派
     */
    public static final Integer TO_BE_ASSIGNED = 0;
    /**
     * 检修任务状态：1待确认
     */
    public static final Integer TO_BE_CONFIRMED = 1;
    /**
     * 检修任务状态：2待执行
     */
    public static final Integer PENDING = 2;
    /**
     * 检修任务状态：3已退回
     */
    public static final Integer GIVE_BACK = 3;
    /**
     * 检修任务状态：4执行中
     */
    public static final Integer IN_EXECUTION = 4;
    /**
     * 检修任务状态：5已驳回
     */
    public static final Integer REJECTED = 5;
    /**
     * 检修任务状态：6待审核
     */
    public static final Integer PENDING_REVIEW = 6;
    /**
     * 检修任务状态：7待验收
     */
    public static final Integer PENDING_RECEIPT = 7;
    /**
     * 检修任务状态：8已完成
     */
    public static final Integer COMPLETED = 8;
    /**
     * 任务来源：1手动领取
     */
    public static final Integer PICK_UP_MANUALLY = 1;
    /**
     * 任务来源：2常规指派
     */
    public static final Integer REGULAR_ASSIGNMENT = 2;
    /**
     * 任务来源：3手工指派
     */
    public static final Integer MANUAL_ASSIGNMENT = 3;

    /**
     * 是否与设备类型相关：0否
     */
    public static final Integer NO_ISAPPOINT_DEVICE = 0;
    /**
     * 是否与设备类型相关：1是
     */
    public static final Integer IS_APPOINT_DEVICE = 1;

    /**
     * 检修管理-手工下发任务状态 1已指派
     */
    public static final Integer ASSIGNED = 1;

    /**
     * 检修周期类型：周检
     */
    public static final Integer WEEK = 0;
    /**
     * 检修周期类型：月检
     */
    public static final Integer MONTH = 1;
    /**
     * 检修周期类型：双月检
     */
    public static final Integer DOUBLEMONTH = 2;
    /**
     * 检修周期类型：季检
     */
    public static final Integer QUARTER = 3;
    /**
     * 检修周期类型：半年检
     */
    public static final Integer SEMIANNUAL = 4;
    /**
     * 检修周期类型：年检
     */
    public static final Integer ANNUAL = 5;
    /**
     * 检修周期类型:半月检
     */
    public static final Integer HALF_MONTH_6 = 6;

    /**
     * 12个月
     */
    public static final Integer MONTHAMOUNT = 12;

    /**
     * 一年4个季度
     */
    public static final Integer QUARTERAMOUNT = 4;
    /**
     * 半年6个月
     */
    public static final Integer SEMIANNUALAMOUNT = 6;
    /**
     * 一个月四周
     */
    public static final Integer MONTH_WEEK_4 = 4;
    /**
     * 上半年和下半年
     */
    public static final Integer HALFYEARAMOUNT = 2;

    /**
     * 是否生效：0否
     */
    public static final Integer NO_IS_EFFECT = 0;

    /**
     * 是否生效：1是
     */
    public static final Integer IS_EFFECT = 1;

    /**
     * 检修结果：1正常
     */
    public static final Integer RESULT_STATUS = 1;

    /**
     * 检修结果：2不正常
     */
    public static final Integer NO_RESULT_STATUS = 2;

    /**
     * 检修值：1无
     */
    public static final Integer NO_STATUS_ITEM = 1;

    /**
     * 检修值：2选择项
     */
    public static final Integer STATUS_ITEM_CHOICE = 2;

    /**
     * 检修值：3输入项
     */
    public static final Integer STATUS_ITEM_INPUT = 3;

    /**
     * 非法操作
     */
    public static final String ILLEGAL_OPERATION = "非法操作";
    /**
     * 参数不全
     */
    public static final String INCOMPLETE_PARAMETERS = "参数不全";
    /**
     * 未查询到相关数据
     */
    public static final String NO_DATA = "未查询到相关数据";
    /**
     * 检修单状态 1 已提交
     */
    public static final Integer SUBMITTED = 1;

    /**
     * 是否生成年计划 0 未生成
     */
    public static final Integer NO_GENERATE = 0;

    /**
     * 是否生成年计划 1 已生成
     */
    public static final Integer GENERATED = 1;
    /**
     * 是否是检查项 1是
     */
    public static final Integer CHECKPROJECT = 1;

    /**
     * 树根节点 0
     */
    public static final String TREE_ROOT_0 = "0";
    /**
     * 有孩子节点
     */
    public static final String HAS_CHILD_1 = "1";

    /**
     * 是否需要审核 0否
     */
    public static final Integer IS_CONFIRM_0 = 0;
    /**
     * 是否需要审核 1是
     */
    public static final Integer IS_CONFIRM_1 = 1;

    /**
     * 故障单状态（已完成）
     */
    public static final Integer FAULT_STATUS = 12;

    /**
     * 故障报修方式（1:报修）
     */
    public static final String FAULT_MODE_CODE_1 = "1";

    /**
     * 故障报修方式（0:自检自修）
     */
    public static final String FAULT_MODE_CODE_0 = "0";
    /**
     * 站点任务：未完成
     */
    public static final Integer INDEX_TASK_STATUS_0 = 0;
    /**
     * 站点任务：已完成
     */
    public static final Integer INDEX_TASK_STATUS_1 = 1;
    /**
     * 首页：全部数据
     */
    public static final Integer IS_ALL_DATA_1 = 1;

    /**
     * 首页：管理部门的数据
     */
    public static final Integer IS_ALL_DATA_0 = 0;
    /**
     * 看板时间字典：本周
     */
    public static final String THIS_WEEK_1 = "1";
    /**
     * 看板时间字典：上周
     */
    public static final String LAST_WEEK_2 = "2";
    /**
     * 看板时间字典：本月
     */
    public static final String THIS_MONTH_3 = "3";
    /**
     * 看板时间字典：上月
     */
    public static final String LAST_MONTH_4 = "4";

    /**
     * 总计划数
     */
    public static final Integer PLAN_TOTAL_1 = 1;
    /**
     * 完成数
     */
    public static final Integer PLAN_FINISH_2 = 2;
    /**
     * 漏检数
     */
    public static final Integer PLAN_MISSED_DETECTION_3 = 3;
    /**
     * 今日检修数
     */
    public static final Integer PLAN_TODAY_4 =4;
}
