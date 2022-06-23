package com.aiurt.modules.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel("设备")
public class DeviceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "专业编码")
    private String majorCode;

    @ApiModelProperty(value = "子系统编码")
    private String systemCode;

    @ApiModelProperty(value = "站所编码")
    private String stationCode;

    @ApiModelProperty(value = "线路")
    private String lineCode;

    @ApiModelProperty(value = "位置")
    private String positionCode;
}
