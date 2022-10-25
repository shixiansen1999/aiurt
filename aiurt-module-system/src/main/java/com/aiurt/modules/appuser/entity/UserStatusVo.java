package com.aiurt.modules.appuser.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserStatusVo {
    @ApiModelProperty(value = "人员状态")
    private String statusName;
    @ApiModelProperty(value = "状态颜色")
    private String statusColor;

}
