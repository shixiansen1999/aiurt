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

}
