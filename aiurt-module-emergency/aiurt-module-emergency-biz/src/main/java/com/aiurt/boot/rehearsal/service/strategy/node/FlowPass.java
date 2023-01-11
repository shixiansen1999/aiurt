package com.aiurt.boot.rehearsal.service.strategy.node;

import com.aiurt.boot.rehearsal.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.rehearsal.service.strategy.NodeAudit;

/**
 * 流程通过审核
 */
public class FlowPass implements NodeAudit {
    @Override
    public EmergencyRehearsalYear audit(EmergencyRehearsalYear rehearsalYear) {
        rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_3);
        return rehearsalYear;
    }
}
