package com.aiurt.modules.complete.dto;

import com.aiurt.modules.flow.dto.NextNodeUserDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author fgw
 */
@Data
public class FlowCompleteReqDTO implements Serializable {

    private static final long serialVersionUID = -6289609606216745945L;


    @ApiModelProperty("业务数据")
    private Map<String, Object> busData;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;


    @ApiModelProperty(value = "下一个节点参与人参数")
    private List<NextNodeUserDTO> nextNodeUserParam;

    /**
     * 流程任务触发按钮类型，内置值可参考FlowTaskButton。
     */
    @ApiModelProperty(value = "流程任务触发按钮类型")
    @NotNull(message = "数据验证失败，任务的审批类型不能为空！")
    @NotBlank(message = "数据验证失败，任务的审批类型不能为空！")
    private String approvalType;

    /**
     * 流程任务的批注内容。
     */
    @ApiModelProperty(value = "流程任务的批注内容")
    private String comment;
}
