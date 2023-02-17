package com.aiurt.common.util;

/**
 * 系统公告自定义跳转方式
 * @author: jeecg-boot
 */
public enum SysAnnmentTypeEnum {
    /**
     * 邮件跳转组件
     */
    EMAIL("email", "component", "modules/eoa/email/modals/EoaEmailInForm"),

    /**
     * 故障
     */
    FAULT("fault", "component",""),
    /**
     * 应急
     */
    EMERGENCY("emergency", "component",""),

    /**
     * 特情
     */
    SITUATION("situation", "component",""),
    /**
     * 培训年计划
     */
    TRAINPLAN("trainplan", "component",""),
    /**
     * 培训复核
     */
    TRAINRECHECK("trainrecheck", "component",""),
    /**
     * 工作日志
     */
    WORKLOG("worklog", "component",""),
    /**
     * 检修指派
     */
    INSPECTION_ASSIGN("inspection_assign", "component",""),
    /**
     * 巡视指派
     */
    PATROL_ASSIGN("patrol_assign", "component",""),
    /**
     * 巡视审核
     */
    PATROL_AUDIT("patrol_audit", "component",""),

    BDOPERATEPLANDECLARATIONFORM("planFromSearch", "component", "prodManage/weekAuditing"),

    /**
     * 通知成为资产盘点人
     */
    ASSET_CHECKER("asset_checker", "component",""),
    /**
    /**
     * 工作流跳转链接我的办公
     */
    BPM("bpm", "url", "/bpm/task/MyTaskList");

    /**
     * 业务类型(email:邮件 bpm:流程)
     */
    private String type;
    /**
     * 打开方式 组件：component 路由：url
     */
    private String openType;
    /**
     * 组件/路由 地址
     */
    private String openPage;

    SysAnnmentTypeEnum(String type, String openType, String openPage) {
        this.type = type;
        this.openType = openType;
        this.openPage = openPage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public String getOpenPage() {
        return openPage;
    }

    public void setOpenPage(String openPage) {
        this.openPage = openPage;
    }

    public static SysAnnmentTypeEnum getByType(String type) {
        if (oConvertUtils.isEmpty(type)) {
            return null;
        }
        for (SysAnnmentTypeEnum val : values()) {
            if (val.getType().equals(type)) {
                return val;
            }
        }
        return null;
    }
}
