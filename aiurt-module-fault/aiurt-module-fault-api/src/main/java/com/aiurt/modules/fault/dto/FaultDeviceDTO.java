package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author
 * @description: 人员画像维修记录故障设备信息
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "历史维修记录-设备故障信息列表", description = "历史维修记录-设备故障信息列表")
public class FaultDeviceDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 故障编号
     */
    @ApiModelProperty(value = "故障编号")
    private String code;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 故障发生位置
     */
    @ApiModelProperty(value = "故障发生位置")
    private String position;

    /**
     * 故障现象
     */
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

    /**
     * 维修完成时间
     */
    @ApiModelProperty(value = "维修完成时间")
    private String endTime;
}
