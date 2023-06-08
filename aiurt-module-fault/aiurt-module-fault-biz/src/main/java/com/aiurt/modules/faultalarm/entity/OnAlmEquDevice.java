package com.aiurt.modules.faultalarm.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author:wgp
 * @create: 2023-06-06 15:47
 * @Description: 系统设备与集中告警设备关联实体
 */
@Data
public class OnAlmEquDevice {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "设备id")
    private String deviceId;
    @ApiModelProperty(value = "集中告警的设备id")
    private String almEquipmentId;
}