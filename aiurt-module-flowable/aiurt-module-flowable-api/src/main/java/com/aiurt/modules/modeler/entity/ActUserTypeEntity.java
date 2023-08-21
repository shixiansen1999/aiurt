package com.aiurt.modules.modeler.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author:wgp
 * @create: 2023-07-24 11:07
 * @Description: 多人审批规则
 */
@Data
public class ActUserTypeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "flowable:userType节点名称")
    private String name;

    @ApiModelProperty(value = "flowable:userType节点值")
    private String value;
}
