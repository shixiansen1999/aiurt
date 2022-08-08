package com.aiurt.modules.flow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fgw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * 业务数据id
     */
    @ApiModelProperty(value = "业务数据id")
    private String businessKey;

    /**
     * 流程定义名称
     */
    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;

    /**
     * 流程定义id
     */
    @ApiModelProperty(value = "流程定义id")
    private String processDefinitionId;


    @ApiModelProperty(value = "流程定义key, 流程标识")
    private String processDefinitionKey;


    @ApiModelProperty(value = "部署id")
    private String deploymentId;


    @ApiModelProperty(value = "花费时间毫秒")
    private Long durationInMillis;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 介绍时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;


}
