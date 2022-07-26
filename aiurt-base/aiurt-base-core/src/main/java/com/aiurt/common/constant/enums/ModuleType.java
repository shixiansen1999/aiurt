package com.aiurt.common.constant.enums;

import cn.hutool.core.util.StrUtil;

/**
 * 日志按模块分类
 * @author: jeecg-boot
 */
public enum ModuleType {

    /**
     * 普通
     */
    COMMON("common", "普通"),

    /**
     * online
     */
    ONLINE("online", "在线开发模块"),

    /**
     * 故障
     */
    FAULT("fault", "故障管理"),

    /**
     * 检修
     */
    INSPECTION("inspection", "检修管理"),

    /**
     *巡检
     */
    PATROL("patrol", "巡检管理"),

    /**
     * 设备
     */
    DEVICE("device", "设备管理");

    private String code;
    private String message;

    private ModuleType(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }



    public String getMessage() {
        return message;
    }




    public static ModuleType getByCode(String code) {
        for (ModuleType moduleType : ModuleType.values()) {
            if (StrUtil.equalsIgnoreCase(code, moduleType.getCode())) {
                return moduleType;
            }
        }
        return null;
    }

}
