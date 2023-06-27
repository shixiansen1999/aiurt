package com.aiurt.modules.fault.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author fgw
 */
public enum FaultDurationEnum {
    /**
     * 0-30分钟
     */
    LESS_THAN_30_MINUTES("0", 0 * 60, 30 * 60),
    /**
     * 30-60分钟
     */
    BETWEEN_30_AND_60_MINUTES("1", 30 * 60, 60 * 60),
    /**
     * 60-120分钟；1-2小时
     */
    BETWEEN_60_AND_120_MINUTES("2", 60 * 60, 120 * 60),
    /**
     * 120-480分钟 2-8小时
     */
    BETWEEN_120_AND_480_MINUTES("3", 120 * 60, 480 * 60),
    /**
     * 480-960分钟 8-16小时
     */
    BETWEEN_480_AND_960_MINUTES("4", 480 * 60, 960 * 60),

    /**
     * 960-1440分钟，16-24小时
     */
    BETWEEN_960_AND_1440_MINUTES("6", 960 * 60, 1440 * 60),
    /**
     * 大于一天
     */
    MORE_THAN_1_DAY("5", 1440 * 60, Integer.MAX_VALUE);

    private int value;

    private String code;
    private Integer startValue;
    private Integer endValue;

    private FaultDurationEnum(String code, Integer startValue, Integer endValue) {
        this.code = code;
        this.startValue = startValue;
        this.endValue = endValue;
    }

    public String getCode() {
        return code;
    }


    public Integer getStartValue() {
        return startValue;
    }

    public Integer getEndValue() {
        return endValue;
    }


    public static FaultDurationEnum getByCode(String code) {
        for (FaultDurationEnum faultDurationEnum : FaultDurationEnum.values()) {
            if (StrUtil.equalsIgnoreCase(code, faultDurationEnum.getCode())) {
                return faultDurationEnum;
            }
        }
        return null;
    }
}
