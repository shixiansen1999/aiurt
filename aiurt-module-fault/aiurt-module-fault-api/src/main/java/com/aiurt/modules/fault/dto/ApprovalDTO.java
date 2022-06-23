package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel("故障审批对象")
public class ApprovalDTO implements Serializable {

    @ApiModelProperty("故障编号")
    @NotBlank(message = "请选择故障编号")
    private String faultCode;

    @ApiModelProperty("审核说明")
    private String approvalRejection;

    @ApiModelProperty("审批状态， 1： 通过， 0：驳回")
    private Integer approvalStatus;
}
