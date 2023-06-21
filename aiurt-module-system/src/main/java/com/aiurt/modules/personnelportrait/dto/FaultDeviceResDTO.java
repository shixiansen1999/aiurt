package com.aiurt.modules.personnelportrait.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 * @description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "故障设备信息", description = "故障设备信息")
public class FaultDeviceResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 故障现象
     */
    @ApiModelProperty(value = "故障现象")
    private String phenomenon;

    /**
     * 开始维修时间
     */
    @ApiModelProperty(value = "开始维修时间")
    private Date time;

}
