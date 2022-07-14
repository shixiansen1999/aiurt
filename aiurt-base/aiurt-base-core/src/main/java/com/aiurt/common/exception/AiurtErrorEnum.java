package com.aiurt.common.exception;

import org.intellij.lang.annotations.Flow;

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
    FLOW_MODEL_NOT_FOUND(61001,"流程模板信息不存在");

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
