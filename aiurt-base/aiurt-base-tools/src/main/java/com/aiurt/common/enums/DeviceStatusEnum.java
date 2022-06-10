package com.aiurt.common.enums;


import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author WangHongTao
 * @Date 2021/11/26
 */
public enum DeviceStatusEnum {

    TY(0, "停用"),
    ZC(1, "正常");

    private int code;
    private String message;

    DeviceStatusEnum(int code, String message){
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
    public static DeviceStatusEnum findByCode(int code){
        if(StrUtil.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<DeviceStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get();
        }
        return null;
    }
    public static String findMessage(int code){
        if(StrUtil.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<DeviceStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get().getMessage();
        }
        return null;
    }
}
