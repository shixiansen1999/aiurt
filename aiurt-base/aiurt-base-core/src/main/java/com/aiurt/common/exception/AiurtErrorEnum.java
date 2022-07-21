package com.aiurt.common.exception;

/**
 *
 */
public enum AiurtErrorEnum {

    /**
     * 故障上报
     */
    NEW_FAULT(1, "故障上报"),

    /***
     * 基础模块 code 1开头 五位数
     */

    /**
     * 6 开头
     */
    FLOW_MODEL_NOT_FOUND(61001,"流程模板信息不存在"),
    FLOW_DEFINITION_NOT_FOUND(61002,"流程模板信息不存在"),

    /**
     * 7 开头
     */
    INVALID_DATA_FIELD(71001,"数据验证失败，无效的数据实体对象字段！");

    private Integer code;
    private String message;

    private AiurtErrorEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }



    public String getMessage() {
        return message;
    }


    public static AiurtErrorEnum getByCode(Integer code) {
        for (AiurtErrorEnum aiurtErrorEnum : AiurtErrorEnum.values()) {
            if ( aiurtErrorEnum.getCode().equals(code)) {
                return aiurtErrorEnum;
            }
        }
        return null;
    }
}
