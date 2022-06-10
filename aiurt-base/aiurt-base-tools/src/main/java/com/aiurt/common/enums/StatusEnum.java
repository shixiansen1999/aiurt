package com.aiurt.common.enums;

public enum StatusEnum {
    ZERO(0),
    ONE(1),
    TWO(2);
    private int code;

    StatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
