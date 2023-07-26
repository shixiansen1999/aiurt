package com.aiurt.modules.user.enums;

/**
 *
 * @desc 流程选人
 * @author fgw
 */
public enum FlowUserRelationEnum {

    /**
     * 发起人部门领导
     */
    INITIATOR_DEPARTMENT_LEADER("initiator_department_leader", "发起人部门领导"),

    /**
     * 发起人上级部门领导
     */
    SUPERIOR_LEADER_OF_INITIATOR("superior_leader_of_initiator", "发起人上级部门领导"),

    /**
     * 流程变量
     */
    PROCESS_VARIABLE("process_variable", "流程变量"),

    /**
     * 自定义实现类
     */
    CUSTOM_IMPLEMENTATION_CLASS("custom_implementation_class", "自定义实现类");


    private String code;
    private String message;

    private FlowUserRelationEnum(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }



    public String getMessage() {
        return message;
    }


    public static FlowUserRelationEnum getByCode(String code) {
        for (FlowUserRelationEnum flowUserRelationEnum : FlowUserRelationEnum.values()) {
            if ( flowUserRelationEnum.getCode().equals(code)) {
                return flowUserRelationEnum;
            }
        }
        return null;
    }
}
