package com.aiurt.common.enums;


import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author WangHongTao
 * @Date 2021/11/19
 */
public enum FaultTypeEnum {

    SBGZ(0, "设备故障"),
    XLGZ(1, "线路故障"),
    DYGZ(2, "电源故障"),
    WJFH(3, "外界妨害"),
    QT(4, "其它");

    private int code;
    private String message;

    FaultTypeEnum(int code, String message){
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
    public static FaultTypeEnum findByCode(int code){
        if(StrUtil.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<FaultTypeEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get();
        }
        return null;
    }
    public static String findMessage(int code){
        if(StrUtil.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<FaultTypeEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get().getMessage();
        }
        return null;
    }
}
