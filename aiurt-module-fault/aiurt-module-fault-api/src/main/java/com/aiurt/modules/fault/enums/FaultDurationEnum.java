package com.aiurt.modules.fault.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author fgw
 */
public enum FaultDurationEnum {

    LESS_THAN_30_MINUTES("0",0,30),
    BETWEEN_30_AND_60_MINUTES("1",30,60),
    BETWEEN_60_AND_120_MINUTES("2",60,120),
    BETWEEN_120_AND_480_MINUTES("3", 120,480),
    BETWEEN_480_AND_960_MINUTES("4",480,960),
    MORE_THAN_1_DAY("5",1440, Integer.MAX_VALUE);

    private int value;

    private String code;
    private Integer startValue;
    private Integer endValue;

    private FaultDurationEnum(String code, Integer startValue, Integer endValue){
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
