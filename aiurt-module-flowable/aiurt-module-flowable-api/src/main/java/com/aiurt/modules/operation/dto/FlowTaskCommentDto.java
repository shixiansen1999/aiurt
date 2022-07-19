package com.aiurt.modules.operation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 流程任务的批注。
 *
 * @author wgp
 * @date 2021-06-06
 */
@ApiModel("流程任务的批注")
@Data
public class FlowTaskCommentDto {

    /**
     * 流程任务触发按钮类型，内置值可参考FlowTaskButton。
     */
    @ApiModelProperty(value = "流程任务触发按钮类型")
    @NotNull(message = "数据验证失败，任务的审批类型不能为空！")
    private String approvalType;

    /**
     * 流程任务的批注内容。
     */
    @ApiModelProperty(value = "流程任务的批注内容")
    @NotBlank(message = "数据验证失败，任务审批内容不能为空！")
    private String comment;

    /**
     * 委托指定人，比如加签、转办等。
     */
    @ApiModelProperty(value = "委托指定人，比如加签、转办等")
    private String delegateAssginee;
}
