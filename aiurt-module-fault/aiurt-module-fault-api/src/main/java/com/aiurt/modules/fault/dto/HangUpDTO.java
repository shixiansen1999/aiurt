package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class HangUpDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("故障编号")
    @NotBlank(message = "请选择故障编号")
    private String faultCode;

    @ApiModelProperty("挂起说明")
    @NotBlank(message = "请填写挂起说明")
    @Length(max = 255, message = "挂起说明擦长度不能超过255")
    private String hangUpReason;
}
