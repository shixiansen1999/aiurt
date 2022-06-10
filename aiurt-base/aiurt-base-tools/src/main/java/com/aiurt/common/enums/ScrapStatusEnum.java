package com.aiurt.common.enums;

import cn.hutool.core.util.StrUtil;
import io.micrometer.core.instrument.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author WangHongTao
 * @Date 2021/11/17
 */
public enum  ScrapStatusEnum {

    WCL(0, "未处理"),
    BX(1, "报修"),
    BF(2, "报废");

    private int code;
    private String message;

    ScrapStatusEnum(int code, String message){
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
    public static ScrapStatusEnum findByCode(int code){
        if(StrUtil.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<ScrapStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get();
        }
        return null;
    }
    public static String findMessage(int code){
        if(StrUtil.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<ScrapStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get().getMessage();
        }
        return null;
    }
}
