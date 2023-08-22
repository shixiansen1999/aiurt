package com.aiurt.modules.user.enums;

/**
 * @author fgw
 */

public enum VariableUserTypeEnum {
    /**
     * 用户
     */
    USER("1", "用户"),

    /**
     * 机构
     */
    ORG("2", "机构"),

    /**
     * 角色
     */
    ROLE("3", "角色"),

    /**
     * 岗位
     */
    POST("4", "岗位");


    private String code;
    private String message;

    private VariableUserTypeEnum(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }



    public String getMessage() {
        return message;
    }


    public static VariableUserTypeEnum getByCode(String code) {
        for (VariableUserTypeEnum variableUserTypeEnum : VariableUserTypeEnum.values()) {
            if ( variableUserTypeEnum.getCode().equals(code)) {
                return variableUserTypeEnum;
            }
        }
        return null;
    }
}
