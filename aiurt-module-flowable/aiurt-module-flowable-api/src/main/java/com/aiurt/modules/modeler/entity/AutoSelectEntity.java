package com.aiurt.modules.modeler.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 * @date 2023-08-03
 * @desc 自动选人
 */
@Data
public class AutoSelectEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "flowable:userType节点值")
    private String value;
}
