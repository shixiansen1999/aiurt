package com.aiurt.common.enums;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author WangHongTao
 * @Date 2021/11/15
 */
public enum  ProductiveTypeEnum {

    SCLX(2, "通用类"),
    FSCLX(1, "专用类");

    private int code;
    private String message;

    ProductiveTypeEnum(int code, String message){
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
    public static ProductiveTypeEnum findByCode(int code){
        if(StringUtils.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<ProductiveTypeEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get();
        }
        return null;
    }
    public static String findMessage(int code){
        if(StringUtils.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<ProductiveTypeEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get().getMessage();
        }
        return null;
    }
}

