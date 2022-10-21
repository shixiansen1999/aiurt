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
}
