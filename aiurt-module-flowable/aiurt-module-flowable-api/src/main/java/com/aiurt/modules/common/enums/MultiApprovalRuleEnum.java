package com.aiurt.modules.common.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author fgw
 */

public enum MultiApprovalRuleEnum {

    /**
     * 任意会签
     */
    TASK_MULTI_INSTANCE_TYPE_1("taskMultiInstanceType-1", "任意会签"),

    /**
     * 并行
     */
    TASK_MULTI_INSTANCE_TYPE_2("taskMultiInstanceType-2", "并行"),

    /**
     * 多人顺序
     */
    TASK_MULTI_INSTANCE_TYPE_3("taskMultiInstanceType-3", "多人顺序");


    private String code;


    private String name;



    private MultiApprovalRuleEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }



    public String getName() {
        return name;
    }



    public static MultiApprovalRuleEnum getByCode(String code) {
        for (MultiApprovalRuleEnum multiApprovalRuleEnum : MultiApprovalRuleEnum.values()) {
            if (StrUtil.equalsIgnoreCase(code, multiApprovalRuleEnum.getCode())) {
                return multiApprovalRuleEnum;
            }
        }
        return null;
    }
}
