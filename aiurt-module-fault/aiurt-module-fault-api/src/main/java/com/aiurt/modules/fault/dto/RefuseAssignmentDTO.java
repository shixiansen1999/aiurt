package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class RefuseAssignmentDTO implements Serializable {

    @ApiModelProperty("故障编号")
    @NotBlank(message = "请选择故障编号")
    private String faultCode;

    @ApiModelProperty("拒收说明")
    private String refuseRemark;


}
