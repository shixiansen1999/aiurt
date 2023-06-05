package com.aiurt.modules.faultalarm.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author:wgp
 * @create: 2023-06-05 09:53
 * @Description: 取消告警请求DTO
 */
@Data
public class CancelAlarmReqDTO {
    @ApiModelProperty(value = "记录ID")
    private String id;

    @ApiModelProperty(value = "取消原因")
    private String cancelReason;

    @ApiModelProperty(value = "备注")
    private String dealRemark;
}
