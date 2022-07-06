package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel("故障作废对象")
public class CancelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "故障编号", required = true)
    @NotBlank(message = "请选择故障编号")
    private String faultCode;

    @ApiModelProperty(value = "作废说明", required = true)
    @NotBlank(message = "请填写作废说明")
    @Length(max = 255, message = "作废说明不能超过255")
    private String cancelRemark;
}
