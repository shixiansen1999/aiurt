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
    BPMN("bpmn", "","流程"),
    /**
     * 检修
     */
    INSPECTION("inspection", "","检修业务流程"),
    /**
     * 巡视
     */
    PATROL("patrol", "","巡视业务流程"),
    /**
     * 故障
     */
    FAULT("fault","","故障管理流程"),
    /**
     * 应急
     */
    EMERGENCY("emergency","","应急管理流程"),
    /**
     * 施工
     */
    WEEK_PLAN("week_plan","","施工管理流程"),
    /**
     * 固定资产
     */
    FIXED_ASSETS("fixed_assets","","固定资产流程"),
    /**
     * 设备申领
     */
    SPARE_PART("spare_part","","物资出入库流程"),
    /**
     * 工作票
     */
    BD_WORK_TITCK("bd_work_titck","","工作票流程");





    /**
     * 业务类型（fault故障，bpmn流程，inspection检修，patrol：巡视）
     */
    private String type;
    /**
     * 组件/路由 地址
     */
    private String url;

    /**
     * 类型名称
     * @param type
     * @param url
     */
    private String module;

    TodoTaskTypeEnum(String type, String url,String module) {
        this.type = type;
        this.url = url;
        this.module = module;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
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
