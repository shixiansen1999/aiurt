package com.aiurt.boot.rehearsal.service.strategy;

import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;

/**
 * @author CJB
 * @Description: 节点审核环境类
 */
public class AuditContext {

    private NodeAudit nodeAudit;

    public AuditContext(NodeAudit nodeAudit) {
        this.nodeAudit = nodeAudit;
    }

    public EmergencyRehearsalYear doAudit(EmergencyRehearsalYear rehearsalYear) {
        return nodeAudit.audit(rehearsalYear);
    }
}
