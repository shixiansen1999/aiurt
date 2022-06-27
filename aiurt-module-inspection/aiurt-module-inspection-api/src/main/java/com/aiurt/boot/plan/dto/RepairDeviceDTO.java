package com.aiurt.boot.plan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title: 检修计划详情设备信息
 * @Description:
 * @date 2022/6/2310:50
 */
@Data
public class RepairDeviceDTO {

    @ApiModelProperty(value = "设备名称")
    private String name;
    @ApiModelProperty(value = "设备编码")
    private String code;
    @ApiModelProperty(value = "设备类型名称")
    private String deviceTypeName;
    @ApiModelProperty(value = "位置名称")
    private String positionCodeName;
    @ApiModelProperty(value = "临时设备名称")
    private String temporaryName;
    @ApiModelProperty(value = "设备状态名称")
    private String statusName;
    @ApiModelProperty(value = "适用专业名称")
    private String majorName;
    @ApiModelProperty(value = "适用专业子系统名称")
    private String subsystemName;
    @ApiModelProperty(value = "站点编码")
    private String positionCode;
    @ApiModelProperty(value = "站点编码")
    private String stationCode;
    @ApiModelProperty(value = "线路编码")
    private String lineCode;
    @ApiModelProperty(value = "是否临时设备")
    private Integer temporary;
    @ApiModelProperty(value = "设备状态")
    private Integer status;
}
