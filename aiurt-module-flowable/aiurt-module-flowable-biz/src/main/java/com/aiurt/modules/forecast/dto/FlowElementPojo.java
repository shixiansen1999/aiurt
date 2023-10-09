package com.aiurt.modules.forecast.dto;

import lombok.Data;

@Data
public class FlowElementPojo {
    private String id;

    private String targetFlowElementId;
    private String resourceFlowElementId;
    private String flowElementType;
}
