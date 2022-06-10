package com.aiurt.common.enums;

/**
 * 备料申领状态
 * @Author km
 * @Date 2021/9/17 9:47
 * @Version 1.0
 */
public enum MaterialApplyStatusEnum {

    UNCHECKED(0, "未审核"),
    CHECKED(1, "已审核"),
    ;

    private int code;

    private String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    MaterialApplyStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code) {
        for (MaterialApplyStatusEnum c : MaterialApplyStatusEnum.values()) {
            if (c.getCode()==code) {
                return c.name;
            }
        }
        return null;
    }
}
