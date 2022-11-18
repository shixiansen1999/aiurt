package com.aiurt.modules.maplocation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2021/4/2612:01
 */
@Data
public class EquipmentHistoryDTO {
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;
    @ApiModelProperty(value = "设备名称")
    private String deviceName;
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    @ApiModelProperty(value = "位置")
    private String position;
    @ApiModelProperty(value = "位置code")
    private String positionCode;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "时间")
    private Date changeTime;
    @ApiModelProperty(value = "类型名称")
    private String typeName;
    @ApiModelProperty(value = "状态")
    private String state;
}
