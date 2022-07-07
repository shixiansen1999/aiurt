package com.aiurt.modules.fault.dto;

import com.aiurt.modules.fault.entity.FaultDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
@ApiModel(value= "设备确认信息")
public class ConfirmDeviceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "故障编号", required = true)
    @NotBlank(message = "请选择故障编号")
    private String faultCode;

    @ApiModelProperty(value = "设备编码")
    private List<FaultDevice> faultDeviceList;
}
