package com.aiurt.boot.rehearsal.service.strategy.node;

import com.aiurt.boot.rehearsal.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.rehearsal.service.strategy.NodeAudit;

/**
 * 演练计划负责人同意
 */
public class ProgramDirectorsApprove implements NodeAudit {

    @Override
    public EmergencyRehearsalYear audit(EmergencyRehearsalYear rehearsalYear) {
        rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_2);
        return rehearsalYear;
    }
}
