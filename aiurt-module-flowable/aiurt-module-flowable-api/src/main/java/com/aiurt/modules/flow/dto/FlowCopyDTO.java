package com.aiurt.modules.flow.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/10/13
 * @time: 11:43
 */

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023-10-13 11:43
 */
@ApiModel("流程抄送DTO返回对象")
@Data
public class FlowCopyDTO {
    @ApiModelProperty(value = "流程抄送Id")
    private String id;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty(value = "流程定义Id")
    private String processDefinitionId;

    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;

    @ApiModelProperty(value = "流程实例Id")
    private String processInstanceId;

    @ApiModelProperty(value = "流程实例名称")
    private String processInstanceName;

    @ApiModelProperty("办理人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String assigne;

    @ApiModelProperty(value = "流程实例发起人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String processInstanceInitiator;

    /**
     * 流程实例创建时间。
     */
    @ApiModelProperty(value = "流程任务创建时间")
    private Date taskCreateTime;

    /**
     * 抄送节点id
     */
    @ApiModelProperty("抄送节点id")
    private String nodeId;
    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String nodeName;

    /**
     * 抄送时间
     */
    @ApiModelProperty("抄送时间")
    private Date createTime;
}
