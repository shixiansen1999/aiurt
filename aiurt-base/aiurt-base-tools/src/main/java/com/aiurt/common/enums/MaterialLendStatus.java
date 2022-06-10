package com.aiurt.common.enums;

/**
 * @Author km
 * @Date 2021/9/22 17:03
 * @Version 1.0
 */
public enum MaterialLendStatus {
    OFF_THE_STOCK(0, "未还"),
    RETURNED(1, "已还"),
    ;

    private int code;

    private String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    MaterialLendStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code) {
        for (MaterialLendStatus c : MaterialLendStatus.values()) {
            if (c.getCode()==code) {
                return c.name;
            }
        }
        return null;
    }
}
