package com.aiurt.boot.rehearsal.service.strategy;

import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;


public interface NodeAudit {
    /**
     * 节点审核接口
     */
    EmergencyRehearsalYear audit(EmergencyRehearsalYear rehearsalYear);
}
