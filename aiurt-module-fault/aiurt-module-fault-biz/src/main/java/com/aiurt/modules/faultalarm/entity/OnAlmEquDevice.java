package com.aiurt.modules.faultalarm.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author:wgp
 * @create: 2023-06-06 15:47
 * @Description:
 */
@Data
public class OnAlmEquDevice {
    @ApiModelProperty(value = "是否受理")
    private String id;
    @ApiModelProperty(value = "设备id")
    private String deviceId;
    @ApiModelProperty(value = "集中告警的设备id")
    private String almEquipmentId;
}