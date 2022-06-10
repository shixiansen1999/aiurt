package com.aiurt.boot.common.enums;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum FaultStatusEnum {

    XINBAOXIU(0, "新报修"),
    WEIXIUZHONG(1, "维修中"),
    WEIXIUWANCHENG(2, "维修完成");

    private int code;
    private String message;

    FaultStatusEnum(int code, String message){
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
    public static FaultStatusEnum findByCode(int code){
        if(StringUtils.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<FaultStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get();
        }
        return null;
    }
    public static String findMessage(int code){
        if(StringUtils.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<FaultStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get().getMessage();
        }
        return null;
    }
}
