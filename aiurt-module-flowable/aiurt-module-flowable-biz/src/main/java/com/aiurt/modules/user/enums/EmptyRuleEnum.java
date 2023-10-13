package com.aiurt.modules.user.enums;

public enum EmptyRuleEnum {

    /**
     * AUTO_COMPLETE
     */
    AUTO_COMPLETE("1", "_AUTO_COMPLETE"),

    /**
     * 转交admin
     */
    AUTO_ADMIN("2", "_AUTO_ADMIN"),

    /**
     * 指定用户
     */
    POINT_USER_NAME("3", "指定用户");


    private String code;
    private String message;

    private EmptyRuleEnum(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }



    public String getMessage() {
        return message;
    }


    public static EmptyRuleEnum getByCode(String code) {
        for (EmptyRuleEnum emptyRuleEnum : EmptyRuleEnum.values()) {
            if ( emptyRuleEnum.getCode().equals(code)) {
                return emptyRuleEnum;
            }
        }
        return null;
    }
}
