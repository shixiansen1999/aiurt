package com.aiurt.modules.flow.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class NextNodeUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "下一个节点数据")
    private String nodeId;

    @ApiModelProperty(value = "下一步办理人")
    private List<String> approver;
}
