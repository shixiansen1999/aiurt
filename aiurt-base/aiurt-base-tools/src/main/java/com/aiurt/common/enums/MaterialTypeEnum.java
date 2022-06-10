package com.aiurt.common.enums;

/**
 * @Author km
 * @Date 2021/9/14 15:11
 * @Version 1.0
 */
public enum MaterialTypeEnum {

    NON_PRODUCTIVE_TYPE(1, "专用类"),
    PRODUCTIVE_TYPE(2, "通用类"),
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
        if(code==null){
            return null;
        }else{
            for (MaterialTypeEnum c : MaterialTypeEnum.values()) {
                if (c.getCode()==code) {
                    return c.name;
                }
            }
        }
        return null;
    }
    public static Integer getCodeByName(String name) {
        for (MaterialTypeEnum c : MaterialTypeEnum.values()) {
            if (c.getName().equals(name)) {
                return c.code;
            }
        }
        return null;
    }
}
