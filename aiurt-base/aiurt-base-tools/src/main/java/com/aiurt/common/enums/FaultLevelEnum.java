package com.aiurt.common.enums;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;


public enum FaultLevelEnum {

    PTGZ(1, "普通故障"),
    ZDGZ(2, "重大故障");

    private int code;
    private String message;

    FaultLevelEnum(int code, String message){
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
    public static FaultLevelEnum findByCode(int code){
        if(StringUtils.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<FaultLevelEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get();
        }
        return null;
    }
    public static String findMessage(int code){
        if(StringUtils.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<FaultLevelEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get().getMessage();
        }
        return null;
    }
}
