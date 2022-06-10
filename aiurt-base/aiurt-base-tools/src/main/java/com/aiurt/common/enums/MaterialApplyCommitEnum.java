package com.aiurt.common.enums;

/**
 * @Author km
 * @Date 2021/9/17 10:20
 * @Version 1.0
 */
public enum MaterialApplyCommitEnum {
    UNCOMMITTED(0, "未提交"),
    COMMITTED(1, "已提交"),
    ;

    private int code;

    private String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    MaterialApplyCommitEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code) {
        for (MaterialApplyCommitEnum c : MaterialApplyCommitEnum.values()) {
            if (c.getCode()==code) {
                return c.name;
            }
        }
        return null;
    }
}
