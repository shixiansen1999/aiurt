package com.aiurt.common.util;

import cn.hutool.core.util.StrUtil;

/**
 * 系统公告自定义跳转方式
 * @author: jeecg-boot
 */
public enum SysAnnmentTypeEnum {
    /**
     * 邮件跳转组件
     */
    EMAIL("email", "component", "modules/eoa/email/modals/EoaEmailInForm",""),

    /**
     * 故障
     */
    FAULT("fault", "component","","故障业务消息"),
    /**
     * 故障
     */
    FAULT_EXTERNAL("fault_external", "component","","故障业务消息"),
    /**
     * 故障
     */
    RECEIVE_FAULT_NO_UPDATE("receive_fault_no_update", "component", "","故障业务消息"),
    /**
     * 故障
     */
    NO_RECEIVE_FAULT("no_receive_fault", "component", "","故障业务消息"),
    /**
     * 故障挂起超时未处理提醒
     */
    HANG_UP_REMIND("hang_up_remind", "component", "","故障业务消息"),
    /**
     * 周计划
     */
    OPERATE_PLAN("week_plan", "component","","周计划业务消息"),
    /**
     * 应急
     */
    EMERGENCY("emergency", "component","","应急业务消息"),

    /**
     * 特情
     */
    SITUATION("situation", "component","","特情消息"),
    /**
     * 培训年计划
     */
    TRAIN_PLAN("train_plan", "component","","培训业务消息"),
    /**
     * 培训复核
     */
    TRAIN_RECHECK("train_recheck", "component","","培训业务消息"),
    /**
     * 工作日志
     */
    WORKLOG("worklog", "component","","工作日志消息"),
    /**
     * 检修
     */
    INSPECTION("inspection", "component","","检修业务消息"),
    /**
     * 检修退回
     */
    INSPECTION_RETURN("inspection_return", "component","","检修业务消息"),
    /**
     * 检修指派
     */
    INSPECTION_ASSIGN("inspection_assign", "component","","检修业务消息"),
    /**
     * 巡视指派
     */
    PATROL_ASSIGN("patrol_assign", "component","","巡视业务消息"),
    /**
     * 巡视审核
     */
    PATROL_AUDIT("patrol_audit", "component","","巡视业务消息"),
    /**
     * 当日15点整时有未完成巡视任务给予提醒
     */
    PATROL_UN_DONE_REMIND("patrol_un_done_remind", "component", "", "巡视业务消息"),
    /**
     *备件归还
     */
    SPAREPART_RETURN("sparepart_return", "component","","物资出入库消息"),
    /**
     *备件出库
     */
    SPAREPART_OUT("sparepart_out", "component","","物资出入库消息"),
    /**
     *备件退库
     */
    SPAREPART_BACK("sparepart_back", "component","","物资出入库消息"),
    /**
     *备件借出
     */
    SPAREPART_LEND("sparepart_lend","component","","物资出入库消息"),
    /**
     *备件申领
     */
    SPAREPART_APPLY("sparepart_apply","component","","物资出入库消息"),
    /**
     *2级库盘点
     */
    SPAREPART_STOCKLEVEL2CHECK("sparepart_stocklevel2check","component","","物资出入库消息"),
    /**
     *2级库出库
     */
    SPAREPART_STOCKLEVEL2SECONDARY("sparepart_stockLevel2Secondary","component","","物资出入库消息"),
    /**
     *备件借出
     */
    SPAREPART_SCRAP("sparepart_scrap","component","","物资出入库消息"),

    BDOPERATEPLANDECLARATIONFORM("planFromSearch", "component", "prodManage/weekAuditing",""),

    /**
     * 通知成为资产盘点人
     */
    ASSET_CHECKER("asset_checker", "component","","固定资产盘点"),
    /**
     * 固定资产消息通知
     */
    ASSET_AUDIT("asset_audit", "component","","固定资产盘点"),
    /**
     * 固定资产盘点结果审核
     */
    FIXED_ASSETS_CHECK("fixed_assets_check", "component","","固定资产盘点"),

    /**
    /**
     * 工作流跳转链接我的办公
     */
    BPM("bpm", "url", "/bpm/task/MyTaskList","");

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
    /**
     * 消息模块
     */
    private String module;

    SysAnnmentTypeEnum(String type, String openType, String openPage,String module) {
        this.type = type;
        this.openType = openType;
        this.openPage = openPage;
        this.module = module;
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

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }


    public static SysAnnmentTypeEnum getByTypeV2(String type) {
        if (oConvertUtils.isEmpty(type)) {
            return null;
        }
        for (SysAnnmentTypeEnum val : values()) {
            String valType = val.getType();
            if (StrUtil.contains(type, valType)){
              return val;
            }
        }
        return null;
    }
}
