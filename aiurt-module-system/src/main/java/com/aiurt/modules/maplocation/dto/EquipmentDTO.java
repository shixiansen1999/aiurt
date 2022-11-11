package com.aiurt.modules.maplocation.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 设备信息以及对应的站点信息实体
 * @date 2021/4/3018:17
 */
@Data
public class EquipmentDTO {
    @ApiModelProperty(value = "设备id")
    private String id; // 设备id
    @ApiModelProperty(value = "对应站点的位置X")
    private Double positionX; // 对应站点的位置X
    @ApiModelProperty(value = "对应站点的位置Y")
    private Double positionY; // 对应站点的位置Y
//    @ApiModelProperty(value = "设备名称")
//    private java.lang.String name;
//    @ApiModelProperty(value = "设备编码")
//    private java.lang.String deviceCode;
//    @ApiModelProperty(value = "设备位置")
//    private java.lang.String position;
//    @ApiModelProperty(value = "状态名称")
//    private java.lang.String stateName;
//    @ApiModelProperty(value = "类型名称")
//    private java.lang.String typeName;
}
