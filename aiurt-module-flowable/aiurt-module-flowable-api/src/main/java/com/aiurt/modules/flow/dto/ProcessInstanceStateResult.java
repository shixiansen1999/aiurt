package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class ProcessInstanceStateResult implements Serializable {

    private static final long serialVersionUID = -6974093892456803852L;

    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    /**
     *
     */
    @ApiModelProperty(value = "数据状态， 1：已结束，0：未结束")
    private Integer processStates;

}
