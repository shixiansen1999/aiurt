package com.aiurt.boot.modules.fault.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

/**
 * @Author: swsc
 * 故障知识库查询参数列表
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class FaultKnowledgeBaseParam {

    /**
     * 故障现象
     */
    private String faultPhenomenon;

    /**
     * 故障原因
     */
    private String faultReason;

    /**
     * 故障措施
     */
    private String solution;
}
