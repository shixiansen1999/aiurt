package com.aiurt.modules.common.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author fgw
 */

public enum FlowConditionTypeEnum {

    /**
     * 数字
     */
    NUM("num", "数字"),
    /**
     * 字符串
     */
    STRING("string", "字符串"),
    /**
     * 数组
     */
    COLLECT("collect", "数组");


    private String code;


    private String name;



    private FlowConditionTypeEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }



    public String getName() {
        return name;
    }



    public static FlowConditionTypeEnum getByCode(String code) {
        for (FlowConditionTypeEnum flowConditionTypeEnum : FlowConditionTypeEnum.values()) {
            if (StrUtil.equalsIgnoreCase(code, flowConditionTypeEnum.getCode())) {
                return flowConditionTypeEnum;
            }
        }
        return null;
    }
}
