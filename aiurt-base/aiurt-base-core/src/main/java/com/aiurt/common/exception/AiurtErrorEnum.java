package com.aiurt.common.exception;

/**
 *
 */
public enum AiurtErrorEnum {

    /**
     * 故障上报
     */
    NEW_FAULT(1, "故障上报");

    /***
     * 基础模块 code 1开头 五位数
     */

    /**
     * 2
     */

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
