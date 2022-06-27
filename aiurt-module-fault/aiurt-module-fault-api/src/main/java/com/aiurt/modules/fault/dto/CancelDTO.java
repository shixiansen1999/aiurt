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
@ApiModel("故障作废对象")
public class CancelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("故障编号")
    @NotBlank(message = "请选择故障编号")
    private String faultCode;

    @ApiModelProperty("作废说明")
    private String cancelRemark;
}
