package com.aiurt.modules.common.enums;

import cn.hutool.core.util.StrUtil;

public enum SystemVariableEnum {

    /**
     *
     */
    SYS_ROLE("sys_role", "角色"),
    /**
     * 机构
     */
    SYS_DEPT("sys_dept", "机构");


    private String code;


    private String name;



    private SystemVariableEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }



    public String getName() {
        return name;
    }



    public static SystemVariableEnum getByCode(String code) {
        for (SystemVariableEnum systemVariableEnum : SystemVariableEnum.values()) {
            if (StrUtil.equalsIgnoreCase(code, systemVariableEnum.getCode())) {
                return systemVariableEnum;
            }
        }
        return null;
    }
}
