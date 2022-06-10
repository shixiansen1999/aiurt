package com.aiurt.common.enums;

public enum IssuanceEnum {
    YOUJIAO("幼教"),
    YIJIAO("义教"),
    PUGAO("普高");
    private String code;

    IssuanceEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
