package com.aiurt.modules.faultalarm.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author:wgp
 * @create: 2023-06-08 09:44
 * @Description: 故障上报后的回调的请求参数DTO
 */
@Data
public class OnFailureReportedReqDTO {
    @ApiModelProperty(value = "记录ID")
    @NotBlank(message = "记录id不能为空")
    private String id;

    @NotBlank(message = "工单编号不能为空")
    @ApiModelProperty(value = "工单编号")
    private String faultCode;
}
