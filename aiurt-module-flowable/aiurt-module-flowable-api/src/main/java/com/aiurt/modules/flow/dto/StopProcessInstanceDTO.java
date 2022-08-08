package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel("终止流程实例")
public class StopProcessInstanceDTO implements Serializable {

    private static final long serialVersionUID = 962897034605105631L;

    @ApiModelProperty(value = "流程实例id", required = true)
    @NotBlank(message = "请输入流程实例")
    private String processInstanceId;

    @ApiModelProperty(value = "终止原因", required = true)
    @NotBlank(message = "请输入终止原因")
    private String stopReason;
}
