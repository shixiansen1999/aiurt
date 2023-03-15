package com.aiurt.common.constant.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.util.oConvertUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 待办任务-任务类型
 * @author: wgp
 * 任务类型（fault故障，bpmn流程，inspection检修，patrol：巡视）
 */
public enum TodoTaskEnum {
    /**
     * 流程
     */
    BPMN("bpmn", Arrays.asList("bpmn"),"流程"),
    /**
     * 检修
     */
    INSPECTION("inspection", Arrays.asList("inspection"),"检修业务流程"),
    /**
     * 巡视
     */
    PATROL("patrol",  Arrays.asList("patrol"),"巡视业务流程"),
    /**
     * 故障
     */
    FAULT("fault", Arrays.asList("fault", "fault_knowledge_base", "fault_analysis_report"),"故障管理流程"),
    /**
     * 应急
     */
    EMERGENCY("emergency", Arrays.asList("emergency_year_plan", "emergency_plan"),"应急管理流程"),
    /**
     * 施工
     */
    WEEK_PLAN("week_plan", Arrays.asList("week_plan_construction", "supplementary_plan"),"施工管理流程"),
    /**
     * 固定资产
     */
    FIXED_ASSETS("fixed_assets", Arrays.asList("fixed_assets_check","fixed_assets"),"固定资产流程"),
    /**
     * 物资出入库
     */
    SPARE_PART("sparepart",  Arrays.asList("sparepart_return", "sparepart_out", "sparepart_back", "sparepart_lend", "sparepart_apply", "sparepart_stocklevel2check", "sparepart_stockLevel2Secondary", "sparepart_scrap", "sparepart_lend_return"),"物资出入库流程"),
    /**
     * 工作票
     */
    BD_WORK_TITCK("bd_work_ticket", Arrays.asList("bd_work_ticket2", "bd_work_titck"),"工作票流程"),


    FAULT_PRODUCE_REPORT("fault_produce_report", Arrays.asList("fault_produce_report"),"工作日志流程");





    /**
     * 业务类型（fault故障，bpmn流程，inspection检修，patrol：巡视）
     */
    private String type;
    /**
     * 组件/路由 地址
     */
    private List<String> list;

    /**
     * 类型名称
     * @param type
     * @param url
     */
    private String module;

    TodoTaskEnum(String type, List<String> list, String module) {
        this.type = type;
        this.list = list;
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

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public static TodoTaskEnum getByType(String type) {
        if (oConvertUtils.isEmpty(type)) {
            return null;
        }
        for (TodoTaskEnum val : values()) {
            if (val.getType().equals(type)) {
                return val;
            }
        }
        return null;
    }

    public static TodoTaskEnum getByTypeV2(String type) {
        if (oConvertUtils.isEmpty(type)) {
            return null;
        }
        for (TodoTaskEnum flowConditionTypeEnum : TodoTaskEnum.values()) {
            List<String> resultList = flowConditionTypeEnum.getList();
            if (CollUtil.isNotEmpty(resultList) && resultList.contains(type)) {
                return flowConditionTypeEnum;
            }
            if (StrUtil.equalsIgnoreCase(type, flowConditionTypeEnum.getType())) {
                return flowConditionTypeEnum;
            }
        }
        return null;
    }
}
