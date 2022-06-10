package com.aiurt.boot.common.enums;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum WorkLogStatusEnum {

    WTJ(0, "未提交"),
    YTJ(1, "已提交");

    private int code;
    private String message;

    WorkLogStatusEnum(int code, String message) {
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

    public static WorkLogStatusEnum findByCode(int code) {
        if (StringUtils.isBlank(String.valueOf(code))) {
            return null;
        }
        Optional<WorkLogStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code == (code)).findFirst();
        if (ad.isPresent()) {
            return ad.get();
        }
        return null;
    }

    public static String findMessage(int code) {
        if (StringUtils.isBlank(String.valueOf(code))) {
            return null;
        }
        Optional<WorkLogStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code == (code)).findFirst();
        if (ad.isPresent()) {
            return ad.get().getMessage();
        }
        return null;
    }
}
