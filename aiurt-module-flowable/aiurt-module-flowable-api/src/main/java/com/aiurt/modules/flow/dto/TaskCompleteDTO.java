package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author fgw
 */
@Data
public class TaskCompleteDTO implements Serializable {

    private static final long serialVersionUID = 3767218686021342739L;

    @ApiModelProperty("业务数据")
    private Map<String, Object> busData;

    @ApiModelProperty("流程审批批注对象")
    private FlowTaskCompleteCommentDTO flowTaskCompleteDTO;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;
}
