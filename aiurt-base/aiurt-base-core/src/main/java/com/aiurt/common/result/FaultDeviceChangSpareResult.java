package com.aiurt.common.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author WangHongTao
 * @Date 2021/11/23
 */
@Data
public class FaultDeviceChangSpareResult {

    @ApiModelProperty(value = "维修记录id")
    private Long id;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更换时间")
    private Date changeTime;

    @ApiModelProperty(value = "备件名称")
    private String newSparePartName;

    @ApiModelProperty(value = "旧备件编号")
    private String oldSparePartCode;

    @ApiModelProperty(value = "新备件编号")
    private String newSparePartCode;

    @ApiModelProperty(value = "系统名称")
    private String systemName;

    @ApiModelProperty(value = "站点名称")
    private String station;

    @ApiModelProperty(value = "故障位置")
    private String location;

    @ApiModelProperty(value = "更换人")
    private String createBy;

}
