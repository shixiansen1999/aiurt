package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class ClaimTaskDTO implements Serializable {

    private static final long serialVersionUID = -2949128250127611542L;

    /**
     * 流程实例id
     */
    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;

    @ApiModelProperty(value = "任务id")
    private String taskId;
}
