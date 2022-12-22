package com.aiurt.common.constant.enums;

import com.aiurt.common.util.oConvertUtils;

/**
 * 待办任务-任务类型
 * @author: wgp
 * 任务类型（fault故障，bpmn流程，inspection检修，patrol：巡视）
 */
public enum TodoTaskTypeEnum {
    /**
     * 流程
     */
    BPMN("bpmn", ""),
    /**
     * 检修
     */
    INSPECTION("inspection", ""),
    /**
     * 巡视
     */
    PATROL("patrol", ""),
    /**
     * 故障
     */
    FAULT("fault","");


    /**
     * 业务类型（fault故障，bpmn流程，inspection检修，patrol：巡视）
     */
    private String type;
    /**
     * 组件/路由 地址
     */
    private String url;

    TodoTaskTypeEnum(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static TodoTaskTypeEnum getByType(String type) {
        if (oConvertUtils.isEmpty(type)) {
            return null;
        }
        for (TodoTaskTypeEnum val : values()) {
            if (val.getType().equals(type)) {
                return val;
            }
        }
        return null;
    }
}
