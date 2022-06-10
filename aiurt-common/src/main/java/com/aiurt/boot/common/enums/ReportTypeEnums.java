package com.aiurt.boot.common.enums;

public enum ReportTypeEnums {
    PER_SCHOOL(1, "学前教育资助申报"),
    COMPULSORY(2, "义务教育资助申报"),
    HIGH_SCHOOL(3, "普通高中资助申报");

    private Integer code;
    private String description;

    ReportTypeEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return this.code;
    }
}
