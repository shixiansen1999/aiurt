package com.aiurt.common.enums;


import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.Optional;

public enum WorkLogConfirmStatusEnum {
    WQR(0, "未确认"),
    YQR(1, "已确认");

    private int code;
    private String message;

    WorkLogConfirmStatusEnum(int code, String message) {
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

    public static WorkLogConfirmStatusEnum findByCode(int code) {
        if (StrUtil.isBlank(String.valueOf(code))) {
            return null;
        }
        Optional<WorkLogConfirmStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code == (code)).findFirst();
        if (ad.isPresent()) {
            return ad.get();
        }
        return null;
    }

    public static String findMessage(int code) {
        if (StrUtil.isBlank(String.valueOf(code))) {
            return null;
        }
        Optional<WorkLogConfirmStatusEnum> ad = Arrays.asList(values()).stream().filter(item -> item.code == (code)).findFirst();
        if (ad.isPresent()) {
            return ad.get().getMessage();
        }
        return null;
    }
}
