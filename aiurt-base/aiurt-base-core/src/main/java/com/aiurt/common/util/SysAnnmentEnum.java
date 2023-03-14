package com.aiurt.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 系统公告自定义跳转方式
 * @author: jeecg-boot
 */
public enum SysAnnmentEnum {
    /**
     * 邮件跳转组件
     */
    EMAIL("email", "component", "modules/eoa/email/modals/EoaEmailInForm","", Arrays.asList("email")),

    /**
     * 故障
     */
    FAULT("fault", "component","","故障业务消息", Arrays.asList("fault", "fault_analysis_report", "fault_knowledge_base")),
    /**
     * 周计划
     */
    OPERATE_PLAN("week", "component","","周计划业务消息", Arrays.asList("week_plan_construction", "supplementary_plan")),
    /**
     * 应急
     */
    EMERGENCY("emergency", "component","","应急业务消息", Arrays.asList("emergency_plan", "emergency_year_plan")),

    /**
     * 特情
     */
    SITUATION("situation", "component","","特情消息", Arrays.asList("situation")),
    /**
     * 培训年计划，培训复核
     */
    TRAIN("train", "component","","培训业务消息", Arrays.asList("train_plan", "train_recheck")),

    /**
     * 工作日志
     */
    WORKLOG("worklog", "component","","工作日志消息", Arrays.asList("worklog", "fault_produce_report")),
    /**
     * 检修
     */
    INSPECTION("inspection", "component","","检修业务消息", Arrays.asList("inspection", "inspection_return", "inspection_assign")),

    /**
     * 巡视指派
     */
    PATROL("patrol", "component","","巡视业务消息", Arrays.asList("patrol_assign", "patrol_audit")),

    /**
     *备件归还
     */
    SPAREPART("sparepart", "component","","物资出入库消息",
            Arrays.asList("sparepart_return", "sparepart_out", "sparepart_back", "sparepart_lend", "sparepart_apply", "sparepart_stocklevel2check", "sparepart_stockLevel2Secondary", "sparepart_scrap")),



    BDOPERATEPLANDECLARATIONFORM("planFromSearch", "component", "prodManage/weekAuditing","", Arrays.asList("planFromSearch")),

    /**
     * 通知成为资产盘点人
     */
    ASSET("asset", "component","","固定资产盘点", Arrays.asList("asset_checker", "asset_audit", "fixed_assets_check"));

    /**
    /**
     * 工作流跳转链接我的办公
     */
    //BPM("bpm", "url", "/bpm/task/MyTaskList","", );

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

    private List<String> list;

    SysAnnmentEnum(String type, String openType, String openPage, String module, List<String> list) {
        this.type = type;
        this.openType = openType;
        this.openPage = openPage;
        this.module = module;
        this.list = list;
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

    public static SysAnnmentEnum getByType(String type) {
        if (oConvertUtils.isEmpty(type)) {
            return null;
        }
        for (SysAnnmentEnum val : values()) {
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

    public List<String> getList() {
       return list;
    }


    public static SysAnnmentEnum getByTypeV2(String type) {
        if (oConvertUtils.isEmpty(type)) {
            return null;
        }
        for (SysAnnmentEnum val : values()) {
            List<String> resultList = val.getList();
            if (CollUtil.isNotEmpty(resultList) && resultList.contains(type)){
              return val;
            }
        }
        return null;
    }
}
