package com.aiurt.modules.faultalarm.constant;

/**
 * @author:wgp
 * @create: 2023-06-06 17:22
 * @Description: 报警常量
 */
public class FaultAlarmConstant {

    /**
     * 报警id组成，有值则代表还在取消报警的范围内
     */
    public static final String FAULT_ALARM_ID = "platform:fault:cache:alarm:id";
    /**
     * 告警信息处理状态1表示未处理
     */
    public static final Integer ALM_DEAL_STATE_1 = 1;
    /**
     * 告警信息处理状态2表示取消告警
     */
    public static final Integer ALM_DEAL_STATE_2 = 2;


}
