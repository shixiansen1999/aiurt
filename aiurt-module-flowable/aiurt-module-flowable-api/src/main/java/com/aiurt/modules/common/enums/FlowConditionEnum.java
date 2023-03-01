package com.aiurt.modules.common.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author fgw
 */

public enum FlowConditionEnum {
    /**
     * 发起人角色
     */
    EQ("eq", "等于"),
    NOT_EQUALS("notEquals", "不等于"),
    GT("gt", "大于"),
    GTE("gte", "大于等于"),
    LT("lt", "小于"),
    LTE("lte", "小于等于"),
    CONTAINS_ANY("containsAny", "包含"),
    EMPTY("empty", "为空"),
    IS_NOT_EMPTY("isNotEmpty", "不为空");

    private String code;


    private String name;



    private FlowConditionEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }



    public String getName() {
        return name;
    }



    public static FlowConditionEnum getByCode(String code) {
        for (FlowConditionEnum flowConditionEnum : FlowConditionEnum.values()) {
            if (StrUtil.equalsIgnoreCase(code, flowConditionEnum.getCode())) {
                return flowConditionEnum;
            }
        }
        return null;
    }
}
