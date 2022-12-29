package com.aiurt.boot.rehearsal.dto;

import com.aiurt.boot.rehearsal.constant.EmergencyConstant;

/**
 * 演练计划状态DTO
 */
public class EmergencyPlanStatusDTO {
    /**
     * 待提交
     */
    private final Integer submitStatus = EmergencyConstant.YEAR_STATUS_1;
    /**
     * 审核中
     */
    private final Integer auditStatus = EmergencyConstant.YEAR_STATUS_2;
    /**
     * 已通过
     */
    private final Integer passStatus = EmergencyConstant.YEAR_STATUS_3;
}
