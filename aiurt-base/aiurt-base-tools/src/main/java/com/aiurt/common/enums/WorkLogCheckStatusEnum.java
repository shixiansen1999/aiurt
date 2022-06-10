package com.aiurt.common.enums;

import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.Optional;

public enum  WorkLogCheckStatusEnum {
    WSH(0, "未审阅"),
    YSH(1, "已审阅");

    private int code;
    private String message;

    WorkLogCheckStatusEnum(int code, String message) {
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

    public static WorkLogCheckStatusEnum findByCode(int code) {
        if (StrUtil.isBlank(String.valueOf(code))) {
            return null;
        }
        Optional<WorkLogCheckStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code == (code)).findFirst();
        if (ad.isPresent()) {
            return ad.get();
        }
        return null;
    }

    public static String findMessage(int code) {
        if (StrUtil.isBlank(String.valueOf(code))) {
            return null;
        }
        Optional<WorkLogCheckStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code == (code)).findFirst();
        if (ad.isPresent()) {
            return ad.get().getMessage();
        }
        return null;
    }
}
