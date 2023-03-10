package com.aiurt.modules.common.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author fgw
 */

public enum SystemVariableEnum {

    /**
     * 发起人角色
     */
    SYS_ROLE_START("sys_role_start", "role", "发起人角色"),

    /**
     * 发起人所属机构
     */
    SYS_DEPT_START("sys_dept_start","role", "发起人所属机构"),
    /**
     *  上一步的办理人角色
     */
    SYS_ROLE_DEAL("sys_role_deal","dept", "上一步的办理人角色"),
    /**
     * 上一步的办理人所属机构
     */
    SYS_DEPT_DEAL("sys_dept_deal","dept", "上一步的办理人所属机构");


    private String code;

    private String type;


    private String name;



    private SystemVariableEnum(String code,String type, String name){
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public String getCode() {
        return code;
    }



    public String getName() {
        return name;
    }

    public String getType() {
        return this.type;
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
