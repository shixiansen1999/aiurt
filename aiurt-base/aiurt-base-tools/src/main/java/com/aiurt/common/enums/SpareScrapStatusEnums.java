package com.aiurt.common.enums;

/**
 * @Author km
 * @Date 2021/9/23 16:17
 * @Version 1.0
 */
public enum SpareScrapStatusEnums {
    UN_DISPOSED(0, "未处理"),
    REPAIRS(1, "报修"),
    SCRAP(2, "报废");


    private int code;

    private String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    SpareScrapStatusEnums(int code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code) {
        for (SpareScrapStatusEnums c : SpareScrapStatusEnums.values()) {
            if (c.getCode()==code) {
                return c.name;
            }
        }
        return null;
    }
}
