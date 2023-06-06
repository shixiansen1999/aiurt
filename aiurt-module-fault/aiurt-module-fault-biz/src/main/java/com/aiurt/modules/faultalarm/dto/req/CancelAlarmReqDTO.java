package com.aiurt.modules.faultalarm.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author:wgp
 * @create: 2023-06-05 09:53
 * @Description: 取消告警请求DTO
 */
@Data
public class CancelAlarmReqDTO {
    @ApiModelProperty(value = "记录ID")
    @NotBlank(message = "记录id不能为空")
    private String id;

    @ApiModelProperty(value = "取消原因")
    @NotBlank(message = "取消原因不能为空")
    private String cancelReason;

    @ApiModelProperty(value = "备注")
    private String dealRemark;
}
