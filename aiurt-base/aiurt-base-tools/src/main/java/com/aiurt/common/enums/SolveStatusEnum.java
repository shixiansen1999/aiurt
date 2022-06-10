package com.aiurt.common.enums;

import cn.hutool.core.util.StrUtil;


import java.util.Arrays;
import java.util.Optional;

/**
 * @Author WangHongTao
 * @Date 2021/11/19
 */
public enum SolveStatusEnum {

    WJJ(2, "未解决"),
    YJJ(1, "已解决");

    private int code;
    private String message;

    SolveStatusEnum(int code, String message){
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
    public static SolveStatusEnum findByCode(int code){
        if(StrUtil.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<SolveStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get();
        }
        return null;
    }
    public static String findMessage(int code){
        if(StrUtil.isBlank(String.valueOf(code))){
            return null;
        }
        Optional<SolveStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code==(code)).findFirst();
        if(ad.isPresent()){
            return ad.get().getMessage();
        }
        return null;
    }
}
