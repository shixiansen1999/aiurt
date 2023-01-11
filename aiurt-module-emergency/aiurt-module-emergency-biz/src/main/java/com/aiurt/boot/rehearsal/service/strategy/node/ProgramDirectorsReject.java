package com.aiurt.boot.rehearsal.service.strategy.node;

import com.aiurt.boot.rehearsal.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.rehearsal.service.strategy.NodeAudit;

/**
 * 演练计划负责人驳回
 */
public class ProgramDirectorsReject implements NodeAudit {
    @Override
    public EmergencyRehearsalYear audit(EmergencyRehearsalYear rehearsalYear) {
        // 更新为待提交状态
        rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_1);
        return rehearsalYear;
    }
}
