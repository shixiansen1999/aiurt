package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 流程任务Vo对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@ApiModel("流程任务DTO返回对象")
@Data
public class FlowTaskDTO {

    /**
     * 流程任务Id。
     */
    @ApiModelProperty(value = "流程任务Id")
    private String taskId;

    /**
     * 流程任务名称。
     */
    @ApiModelProperty(value = "流程任务名称")
    private String taskName;

    /**
     * 流程任务标识。
     */
    @ApiModelProperty(value = "流程任务标识")
    private String taskKey;

    /**
     * 任务的表单信息。
     */
    @ApiModelProperty(value = "任务的表单信息")
    private String taskFormKey;

    /**
     * 流程Id。
     */
    @ApiModelProperty(value = "流程Id")
    private Long entryId;

    /**
     * 流程定义Id。
     */
    @ApiModelProperty(value = "流程定义Id")
    private String processDefinitionId;

    /**
     * 流程定义名称。
     */
    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;

    /**
     * 流程定义标识。
     */
    @ApiModelProperty(value = "流程定义标识")
    private String processDefinitionKey;

    /**
     * 流程定义版本。
     */
    @ApiModelProperty(value = "流程定义版本")
    private Integer processDefinitionVersion;

    /**
     * 流程实例Id。
     */
    @ApiModelProperty(value = "流程实例Id")
    private String processInstanceId;

    /**
     * 流程实例发起人。
     */
    @ApiModelProperty(value = "流程实例发起人")
    private String processInstanceInitiator;

    @ApiModelProperty(value = "发起人昵称")
    private String processInstanceInitiatorName;
    /**
     * 流程实例创建时间。
     */
    @ApiModelProperty(value = "流程实例创建时间")
    private Date processInstanceStartTime;

    /**
     * 流程实例主表业务数据主键。
     */
    @ApiModelProperty(value = "流程实例主表业务数据主键")
    private String businessKey;
}
