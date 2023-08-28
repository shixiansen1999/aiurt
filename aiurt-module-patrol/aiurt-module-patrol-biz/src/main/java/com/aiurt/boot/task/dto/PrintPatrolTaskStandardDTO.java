package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author qkx
 */
@Data
public class PrintPatrolTaskStandardDTO {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "车站")
    private String stationNames;

    @ApiModelProperty(value = "巡视人")
    private String userName;

    @ApiModelProperty(value = "抽检人")
    private String spotCheckUserName;

    @ApiModelProperty(value = "提交时间")
    private String submitTime;

    @ApiModelProperty(value = "抽检时间")
    private String spotCheckTime;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备位置")
    private String deviceLocation;

    @ApiModelProperty(value = "巡检频次")
    private String period;

    @ApiModelProperty(value = "巡视工单")
    private List<PrintStandardDTO> printStandardDTOList;

    @ApiModelProperty(value = "任务提交的用户签名图片")
    private String signUrl;

}
