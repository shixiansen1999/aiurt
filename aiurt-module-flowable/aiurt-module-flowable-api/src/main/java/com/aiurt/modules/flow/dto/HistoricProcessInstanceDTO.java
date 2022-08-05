package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel(value = "流程实例历史")
public class HistoricProcessInstanceDTO implements Serializable {

    private static final long serialVersionUID = -5616653776547840132L;

    /**
     * 流程发起人账号
     */
    @ApiModelProperty(value = "流程发起人账号")
    private String userName;

    /**
     * 流程发起人名称
     */
    @ApiModelProperty(value = "流程发起人名称")
    private String realName;

    /**
     * 流程实例名称
     */
    @ApiModelProperty(value = "流程实例名称")
    private String name;

    /**
     * 流程实例id
     */
    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;

    @ApiModelProperty(value = "业务数据id")
    private String businessKey;

    /**
     * 流程定义名称
     */
    private String processDefinitionName;

    /**
     *
     */
    private String processDefinitionId;

}
