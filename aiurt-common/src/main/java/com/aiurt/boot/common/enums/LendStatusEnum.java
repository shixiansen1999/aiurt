package com.aiurt.boot.common.enums;

/**
 * @Author WangHongTao
 * @Date 2021/11/17
 */
public enum LendStatusEnum {

    YH(1, "已还"),
    WH(0, "未还"),
    ;

    private int code;

    private String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    LendStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code) {
        if(code==null){
            return null;
        }else{
            for (LendStatusEnum c : LendStatusEnum.values()) {
                if (c.getCode()==code) {
                    return c.name;
                }
            }
        }
        return null;
    }
    public static Integer getCodeByName(String name) {
        for (LendStatusEnum c : LendStatusEnum.values()) {
            if (c.getName().equals(name)) {
                return c.code;
            }
        }
        return null;
    }
}
