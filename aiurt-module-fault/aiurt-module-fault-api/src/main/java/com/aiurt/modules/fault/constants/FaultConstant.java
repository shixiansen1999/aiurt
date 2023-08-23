package com.aiurt.modules.fault.constants;

/**
 * @author zwl
 * @Title:
 * @Description:
 * @date 2022/9/0716:40
 */
public class FaultConstant {

    /**
     * 故障单状态（已完成）
     */
    public static final Integer FAULT_STATUS = 12;

    /**
     * 故障单状态（待审核）
     */
    public static final Integer FAULT_REVIEWED = 1;

    /**
     * 故障报修方式（1:报修）
     */
    public static final String FAULT_MODE_CODE_1 = "1";

    /**
     * 故障报修方式（0:自检自修）
     */
    public static final String FAULT_MODE_CODE_0 = "0";

    /**
     * 专业子系统
     */
    public static final String SUBSYSTEM = "subsystem";

    /**
     * 专业
     */
    public static final String MAJOR = "major";

    /**
     *  判断条件
     */
    public static final int FAULT_SIZE = 5;

    /**
     *  判断条件
     */
    public static final int FAULT_START = 0;

    /**
     * 配置是否开启,1:开启
     */
    public static final String ENABLE = "1";

    /**
     *  1表示人员当班中的状态
     */
    public static final String ON_DUTY_1 = "1";

    /**
     *  0表示人员休息的状态
     */
    public static final String REST_0 = "0";
    /**
     *  表示人员当班中
     */
    public static final String ON_DUTY_NAME = "当班";
    /**
     *  表示人员休息
     */
    public static final String REST_NAME = "休息";

    /**
     *  表示人员的任务情况为空闲
     */
    public static final String FREE_NAME = "空闲";
    /**
     *  表示人员的任务情况为维修中
     */
    public static final String IN_MAINTENANCE_NAME = "维修中";
    /**
     *  表示取数据的前5条
     */
    public static final int FIRST_5 = 5;
    /**
     * 表示当前人员没有位置信息
     */
    public static final String UNKNOWN_LOCATION = "暂无位置信息";
    /**
     * 是否是信号故障（0信号故障；1非信号故障)
     */
    public static final Integer IS_SIGNAL_FAULT_0 = 0;
    /**
     * 是否是信号故障（0信号故障；1非信号故障)
     */
    public static final Integer IS_SIGNAL_FAULT_1 = 1;
    /**
     * 控制中心审核状态（0待审核，1已通过，2已驳回）
     */
    public static final Integer CONTROL_CENTER_REVIEW_STATUS_0 = 0;
    /**
     * 控制中心审核状态（0待审核，1已通过，2已驳回）
     */
    public static final Integer CONTROL_CENTER_REVIEW_STATUS_1 = 1;
    /**
     * 控制中心审核状态（0待审核，1已通过，2已驳回）
     */
    public static final Integer CONTROL_CENTER_REVIEW_STATUS_2 = 2;
}
