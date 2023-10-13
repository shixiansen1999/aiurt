package com.aiurt.modules.forecast.dto;

import lombok.Data;

/**
 * @author fgw
 */
@Data
public class FlowElementDTO {
    /**
     * 流转线id
     */
    private String id;

    /**
     * 目标节点id
     */
    private String targetFlowElementId;

    /**
     * 源节点id
     */
    private String resourceFlowElementId;

    /**
     * 类型
     */
    private String flowElementType;
}
