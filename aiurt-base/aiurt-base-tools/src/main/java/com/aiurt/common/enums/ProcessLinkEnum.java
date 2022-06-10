package com.aiurt.common.enums;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * 运转流程枚举类
 * @author WangHongTao
 */
public enum ProcessLinkEnum {

    GZDJ(0, "故障登记"),
    ZP(1, "指派"),
    CHZP(2, "重新指派"),
    GQ(3, "挂起"),
    QXGQ(4, "取消挂起"),
    TXWXJL(5, "填写维修记录");

    private int code;
    private String message;

    ProcessLinkEnum(int code, String message){
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public static ProcessLinkEnum findByCode(int code){
        if(StringUtils.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<ProcessLinkEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get();
        }
        return null;
    }
    public static String findMessage(int code){
        if(StringUtils.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<ProcessLinkEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get().getMessage();
        }
        return null;
    }
}
