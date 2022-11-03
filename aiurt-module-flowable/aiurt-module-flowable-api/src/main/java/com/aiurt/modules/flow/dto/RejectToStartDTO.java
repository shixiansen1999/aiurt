package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author fgw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectToStartDTO implements Serializable {

    private static final long serialVersionUID = -6986343140248487128L;

    @ApiModelProperty("业务数据")
    private Map<String, Object> busData;

    @ApiModelProperty("流程审批批注对象")
    private FlowTaskCompleteCommentDTO flowTaskCompleteDTO;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;
}
