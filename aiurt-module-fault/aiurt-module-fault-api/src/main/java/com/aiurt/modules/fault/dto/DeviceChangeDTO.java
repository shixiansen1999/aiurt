package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("组件更换信息")
public class DeviceChangeDTO implements Serializable {

    /**设备id*/
    @ApiModelProperty(value = "设备编码")
    private Integer deviceCode;

    /**原备件编号*/
    @ApiModelProperty(value = "原组件编号")
    private String oldSparePartCode;

    /**原备件数量*/
    @ApiModelProperty(value = "原组件数量")
    private Integer oldSparePartNum;


    /**新备件编号*/
    @ApiModelProperty(value = "新组件编号")
    private String newSparePartCode;

    /**新备件数量*/
    @ApiModelProperty(value = "新组件数量")
    private Integer newSparePartNum;
}
