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
     * 巡视模块最后的工单是否需要自动提交，并写入签名
     */
    String PATROL_SUBMIT_SIGNATURE = "patrol_submit_signature";
    /**
     * 巡检星期，星期几执行检测漏检
     */
    String PATROL_WEEKDAYS = "patrol_weekdays";
    /**
     * 检修模块是否关联排班标识
     */
    String INSPECTION_SCHEDULING = "inspection_scheduling";
    /**
     * 检修模块最后的工单是否需要自动提交，并写入签名
     */
    String INSPECTION_SUBMIT_SIGNATURE = "inspection_submit_signature";
    /**
     * 故障模块是否关联排班标识
     */
    String FAULT_SCHEDULING = "fault_scheduling";
    /**
     * 故障上报是否需要审核
     */
    String FAULT_PROCESS = "fault_process";
    /**
     * 故障提交是否自动提交签名
     */
    String FAULT_SUBMIT_SIGNATURE = "fault_submit_signature";
    /**
     * 故障是否需要审核
     */
    String FAULT_AUDIT = "fault_audit";
    /**
     * 工时统计,是否过滤掉有挂起记录的故障单
     */
    String FAULT_FILTER = "fault_filter";
    /**
     * 工作日志上报是否需要在指定时间点内编辑
     */
    String WORKLOG_EDIT = "worklog_edit";
    /**
     * 故障上报单个设备一个月内重复出现两次故障，系统是否自动发布一条特情
     */
    String FAULT_SITUATION = "fault_situation";
    /**
     * 备件报损是否使用专用导出
     */
    String SPAREPARTSCRAP_SPECIAL_EXPORT = "sparepartscrap_special_export";
    /**
     * 工作日志确认-谁点确认，谁就是接班人
     */
    String WORKLOG_CONFIRM = "worklog_confirm";
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
     * 故障通知
     */
    String FAULT = "fault";
    /**
     * 检修通知
     */
    String INSPECTION = "inspection";
    /**
     * 应急通知
     */
    String EMERGENCY = "emergency";
    /**
     * 巡视通知
     */
    String PATROL = "patrol";
    /**
     * 施工流程待办
     */
    String WEEK_PLAN = "week_plan";
    /**
     * 培训业务
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
     * 故障流程
     */
    String FAULT_FLOW = "fault_flow";
    /**
     * 检修流程
     */
    String INSPECTION_FLOW = "inspection_flow";
    /**
     * 应急流程
     */
    String EMERGENCY_FLOW = "emergency_flow";
    /**
     * 巡视流程
     */
    String PATROL_FLOW = "patrol_flow";
    /**
     * 固定资产流程
     */
    String FIXED_ASSETS = "fixed_assets";
    /**
     * 固定资产
     */
    String ASSET = "asset";

    /**
     * 工作票
     */
    String BD_WORK_TITCK = "bd_work_titck";

    /**
     * 工作日志
     */
    String WORKLOG = "worklog";

    /**
     * 物资出入库流程
     */
    String SPAREPART_FLOW = "sparepart_flow";

    /**
     * 物资出入库
     */
    String SPAREPART = "sparepart";
    /**
     * 施工计划通知
     */
    String WEEK = "week";
    /**
     * 工作日志早上停止编辑时间
     */
    String WORKLOG_AM_STOPEDIT = "worklog_am_stopedit";
    /**
     * 工作日志下午停止编辑时间
     */
    String WORKLOG_PM_STOPEDIT = "worklog_pm_stopedit";
    /**
     * 工作日志早上开始编辑时间
     */
    String WORKLOG_AM_STARTEDIT	 = "WORKLOG_AM_STARTEDIT";
    /**
     * 工作日志下午开始编辑时间
     */
    String WORKLOG_PM_STARTEDIT	 = "worklog_pm_startedit";
    /**
     * 工作内容安排默认内容
     */
    String 	WORK_SCHEDULE = "work_schedule";
    /**
     * 工作内容注意事项默认内容
     */
    String WORK_NOTE = "work_note";
    /**
     * 测试部门
     */
    String TEST_ORGCODE = "test_orgcode";
    /**
     * 故障上报是否需要根据故障级别自动抄送
     */
    String AUTO_CC = "auto_cc";
    /**
     * 	故障级别权重小于等于5抄送班组长
     */
    String FAULT_LEVEL_LITTLE = "fault_level_little";
    /**
     * 故障级别权重大于5抄送线路负责人及分部主任角色包含班组长
     */
    String FAULT_LEVEL_HIGH = "fault_level_high";
    /**
     * 根据配置决定工作日志是否需要自动签名
     */
    String AUTO_SIGNATURE = "auto_signature";
    /**
     * 根据配置决定是否需要把工单数量作为任务数量
     */
    String PATROL_TASK_DEVICE_NUM = "patrol_task_device_num";

    /**
     * 更加配置获取更新upload_time时间间隔
     */
    String WIFI_UPDATE_INTERVAL = "wifi_update_interval";
    /**
     * 根据配置获取是否要筛选掉的旧线路数据
     */
    String OLD_LINE = "old_line";
    /**
     * 根据配置获取要筛选掉的旧线路数据
     */
    String OLD_LINECODE = "old_linecode";
    /**
     * app-检修计划-站点-中心班组的角色有哪些，用于看到特点的站点
     */
    String CENTER_ROLE_CODE = "center_role_code";
    /**
     * app-检修计划-站点-中心班组需要看到的站点
     */
    String INSPECTION_STATION_CODE = "inspection_station_code";
    /**
     * 调度下发新的故障-需要响铃的中心班组
     */
    String FAULT_EXTERNAL_ORG = "fault_external_org";

    /**
     * 根据配置决定故障上报是否开启控制中心班组自检故障指派功能及权限
     */
    String FAULT_CENTER_ADD = "fault_center_add";
    /**
     * 根据配置获取控制中心班组
     */
    String FAULT_CENTER_ADD_ORG = "fault_center_add_org";
    /**
     * 根据配置获取控制中心站点
     */
    String FAULT_CENTER_ADD_STATION = "fault_center_add_station";
    /**
     * 根据配置决定控制中心成员能否领取正线站点故障，开启时表示不能
     */
    String FAULT_CENTER_RECEIVE = "fault_center_receive";
    /**
     * 调度下发之后，填写维修单解决后，回调云轨调度系统的接口地址
     */
    String FAULT_EXTERNAL_URL = "fault_external_url";
    /**
     * 故障调度处理完，回调云轨的系统id
     */
    String FAULT_EXTERNAL_SYSTEM_ID = "fault_external_system_id";
    /**
     * 统计报表是否需要过滤通信分部
     */
    String 	FILTERING_TEAM = "filtering_team";
    /**
     * 通信分部编码
     */
    String 	SPECIAL_TEAM = "special_team";

    /**
     * app端检修工单填写同行人是否可选全部班组
     */
    String INSPECTION_PEER_ALL_USER = "inspection_peer_all_user";

    String INSPECTION_SIGN_MULTI = "inspection_sign_multi";
    /**
     * 	工作内容是否需要查出所有未完成故障
     */
    String 	WORKLOG_UNFINISH_FAULT = "worklog_unfinish_fault";
    String APP_PATROL_TASK_POOL_SORT = "app_patrol_task_pool_sort";
    /**
     * 取消去办理的消息类型
     */
    String NO_DEAL_MESSAGE_TYPE = "no_deal_message_type";
    /**
     * 故障未领取时要给予当班人员提示音
     */
    String NO_RECEIVE_FAULT_REMIND = "no_receive_fault_remind";
    /**
     * 故障领取后两小时未更新任务状态需给予维修人提示音
     */
    String RECEIVE_FAULT_NO_UPDATE = "receive_fault_no_update";
    /**
     * 故障领取后多久未更新任务状态需给予维修人提示音，单位为秒
     */
    String NO_UPDATE_DELAY = "no_update_delay";
    /**
     * 故障领取后未更新任务状态需给予维修人提示音的间隔，单位为秒
     */
    String NO_UPDATE_PERIOD = "no_update_period";
    /**
     * 故障多久未领取时要给予当班人员提示音，单位为秒
     */
    String NO_RECEIVE_DELAY = "no_receive_delay";
    /**
     * 故障未领取时要给予当班人员提示音的间隔，单位为秒
     */
    String NO_RECEIVE_PERIOD = "no_receive_period";
    /**
     * 故障领取后两小时未更新任务状态需给予维修人提示音（每两小时提醒5秒）
     */
    String FAULT_RECEIVE_NO_UPDATE_RING_DURATION = "fault_receive_no_update_ring_duration";
    /**
     * 故障未领取时要给予当班人员提示音（每两分钟提醒20秒）
     */
    String NO_RECEIVE_FAULT_RING_DURATION = "no_receive_fault_ring_duration";
    /**
     * 企业微信跳转地址
     */
    String WECHAT_MESSAGE_URL = "wechat_message_url";
    /**
     * 根据配置，获取施工计划导入列表
     */
    String CONSTRUCTION_WEEK_PLAN_COMMAND = "construction_week_plan_command";

    /**
     * 根据配置，传参部门，获取施工计划导入列表
     */
    String DEPARTMENT_NAME = "department_name";
    /**
     * 根据配置，传参状态，获取施工计划导入列表
     */
    String PLAN_ISTATE = "planIstate";
    /**
     * 根据配置，传参开始时间，获取施工计划导入列表
     */
    String LAST_MONTH = "start_month";
    /**
     * 根据配置，传参结束时间，获取施工计划导入列表
     */
    String NEW_MONTH = "end_month";


}
