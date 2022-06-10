package com.aiurt.boot.common.enums;

/**
 * @Author km
 * @Date 2021/9/14 15:11
 * @Version 1.0
 */
public enum MaterialTypeEnum {

    NON_PRODUCTIVE_TYPE(1, "非生产类型"),
    PRODUCTIVE_TYPE(2, "生产类型"),
    ;

    private int code;

    private String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    MaterialTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code) {
        for (MaterialTypeEnum c : MaterialTypeEnum.values()) {
            if (c.getCode()==code) {
                return c.name;
            }
        }
        return null;
    }
}
