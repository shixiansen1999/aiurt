package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel("组件更换信息")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceChangeDTO implements Serializable {

    /**设备id*/
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;
    @ApiModelProperty(value = "设备编码")
    private String deviceName;

    /**原备件编号*/
    @ApiModelProperty(value = "原组件编号")
    private String oldSparePartCode;

    @ApiModelProperty(value = "原组件名称")
    private String oldSparePartName;

    /**原备件数量*/
    @ApiModelProperty(value = "原组件数量")
    private Integer oldSparePartNum;


    /**新备件编号*/
    @ApiModelProperty(value = "新组件编号")
    private String newSparePartCode;

    @ApiModelProperty(value = "新组件名称")
    private String newSparePartName;

    /**新备件数量*/
    @ApiModelProperty(value = "新组件数量")
    private Integer newSparePartNum;

    @ApiModelProperty(value = "主键id")
    private String id;

    /**维修记录id*/
    @ApiModelProperty(value = "维修记录id")
    private String repairRecordId;

    @ApiModelProperty(value = "出库记录表ID")
    private String outOrderId;

    @ApiModelProperty(value = "规格")
    private String specifications;
}
