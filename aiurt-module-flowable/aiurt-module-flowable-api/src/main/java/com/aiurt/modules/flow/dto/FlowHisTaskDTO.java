package com.aiurt.modules.flow.dto;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/8/315:23
 */
@Data
public class FlowHisTaskDTO {
    /**
     * 流程任务Id。
     */
    @ApiModelProperty(value = "流程任务Id")
    private String id;
    /**
     * 流程实例Id
     */
    @ApiModelProperty(value = "流程实例Id")
    private String processInstanceId;
    /**
     * 流程定义Id
     */
    @ApiModelProperty(value = "流程定义Id")
    private String processDefinitionId;
    /**
     * 流程定义名称
     */
    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;
    /**
     * 流程任务名称。
     */
    @ApiModelProperty(value = "流程任务名称")
    private String taskName;

    /**
     * 审批类型。
     */
    @ApiModelProperty(value = "审批类型")
    @Dict(dictTable = "act_custom_button", dicText = "button_name", dicCode = "button_code")
    private String approvalType;

    /**
     * 流程定义标识。
     */
    @ApiModelProperty(value = "流程定义标识")
    private String processDefinitionKey;
    /**
     * 业务数据key。
     */
    @ApiModelProperty(value = "业务数据key")
    private String businessKey;


    /**
     * 流程发起人。
     */
    @ApiModelProperty(value = "流程发起人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String startUser;

    /**
     * 流程实例创建时间。
     */
    @ApiModelProperty(value = "流程实例创建时间")
    private Date processInstanceStartTime;

    /**
     * 任务的表单信息。
     */
    @ApiModelProperty(value = "任务的表单信息")
    private String formKey;

    @ApiModelProperty(value = "流程实例名称")
    private String processInstanceName;
}
