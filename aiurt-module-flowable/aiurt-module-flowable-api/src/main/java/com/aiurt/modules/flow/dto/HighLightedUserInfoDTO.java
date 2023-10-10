package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class HighLightedUserInfoDTO implements Serializable {

    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("用户名， 分号隔开")
    private String realName;
}
