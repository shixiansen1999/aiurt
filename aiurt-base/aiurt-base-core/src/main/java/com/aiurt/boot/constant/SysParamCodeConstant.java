package com.aiurt.boot.constant;

/**
 * 实施配置-系统参数编码常量
 */
public interface SysParamCodeConstant {
    /**
     * 巡视模块是否关联排班标识
     */
    String PATROL_SCHEDULING = "patrol_scheduling";
    /**
     * 检修模块是否关联排班标识
     */
    String INSPECTION_SCHEDULING = "inspection_scheduling";
    /**
     * 故障模块是否关联排班标识
     */
    String FAULT_SCHEDULING = "fault_scheduling";
    /**
     * 固定资产业务消息发送渠道
     */
    String FIXED_ASSETS_MESSAGE = "fixed_assets_message";
    /**
     * 特情消息发送渠道
     */
    String SPECIAL_INFO_MESSAGE = "special_info_message";
    /**
     * 培训消息发送渠道
     */
    String TRAIN_PLAN_MESSAGE = "train_plan_message";
    /**
     * 故障消息发送渠道
     */
    String FAULT_MESSAGE = "fault_message";
    /**
     * 巡视消息发送渠道
     */
    String PATROL_MESSAGE = "patrol_message";
    /**
     * 检修消息发送渠道
     */
    String REPAIR_MESSAGE = "repair_message";
    /**
     * 工作日志消息发送渠道
     */
    String WORK_LOG_MESSAGE = "repair_message";
    /**
     * 周计划消息发送渠道
     */
    String OPERATE_PLAN_MESSAGE = "operate_plan_message";
    /**
     * 应急管理消息发送渠道
     */
    String EMERGENCY_MANAGEMENT_MESSAGE = "emergency_management_message";
    /**
     * 流程消息发送渠道
     */
    String BPM_MESSAGE = "bpm_message";
}
