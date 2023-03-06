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
     * 固定资产业务消息发送渠道(代办)
     */
    String FIXED_ASSETS_MESSAGE_PROCESS = "fixed_assets_message_process";
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
     * 故障消息发送渠道(待办)
     */
    String FAULT_MESSAGE_PROCESS = "fault_message_process";
    /**
     * 巡视消息发送渠道
     */
    String PATROL_MESSAGE = "patrol_message";
    /**
     * 巡视消息发送渠道（待办）
     */
    String PATROL_MESSAGE_PROCESS = "patrol_message_process";
    /**
     * 检修消息发送渠道
     */
    String REPAIR_MESSAGE = "repair_message";
    /**
     * 检修消息发送渠道（待办）
     */
    String REPAIR_MESSAGE_PROCESS = "repair_message_process";
    /**
     * 工作日志消息发送渠道
     */
    String WORK_LOG_MESSAGE = "work_log_message";
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
    /**
     * 物资出入库发送渠道
     */
    String SPAREPART_MESSAGE = "sparepart_message";
    /**
     * 物资出入库发送渠道（待办）
     */
    String SPAREPART_MESSAGE_PROCESS= "sparepart_message_process";

    /**
     * 故障
     */
    String FAULT = "fault";
    /**
     * 检修
     */
    String INSPECTION = "inspection";
    /**
     * 应急
     */
    String EMERGENCY = "emergency";
    /**
     * 巡视
     */
    String PATROL = "patrol";
    /**
     * 施工
     */
    String WEEK_PLAN = "week_plan";
    /**
     * 培训
     */
    String TRAIN = "train";
    /**
     * 特情消息
     */
    String SITUATION = "situation";
    /**
     * 系统公告
     */
    String SYS_ANNOUNCEMENT ="sys_announcement";
    /**
     * 系统消息
     */
    String SYS_MESSAGE = "sys_message";
    /**
     * 故障
     */
    String FAULT_FLOW = "fault_flow";
    /**
     * 检修
     */
    String INSPECTION_FLOW = "inspection_flow";
    /**
     * 应急
     */
    String EMERGENCY_FLOW = "emergency_flow";
    /**
     * 巡视
     */
    String PATROL_FLOW = "patrol_flow";
    /**
     * 固定资产
     */
    String FIXED_ASSETS = "fixed_assets";

    /**
     * 工作票
     */
    String BD_WORK_TITCK = "bd_work_titck";

    /**
     * 工作日志
     */
    String WORKLOG = "worklog";

}
