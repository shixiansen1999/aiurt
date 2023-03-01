package com.aiurt.modules.common.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author fgw
 */

public enum SystemVariableEnum {

    /**
     * 发起人角色
     */
    SYS_ROLE_START("sys_role_start", "发起人角色"),

    /**
     * 发起人所属机构
     */
    SYS_DEPT_START("sys_dept_start", "发起人所属机构"),
    /**
     *  上一步的办理人角色
     */
    SYS_ROLE_deal("sys_role_deal", "上一步的办理人角色"),
    /**
     * 上一步的办理人所属机构
     */
    SYS_DEPT_deal("sys_dept_deal", "上一步的办理人所属机构");


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
