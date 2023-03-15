package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author LKJ
 */
@Data
public class PrintPatrolTaskDTO {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "车站")
    private String stationNames;

    @ApiModelProperty(value = "巡视人")
    private String userName;

    @ApiModelProperty(value = "提交时间")
    private String submitTime;

    private List<PrintStationDTO> printStationDTOList;

    @ApiModelProperty(value = "任务提交的用户签名图片")
    private java.lang.String signUrl;

}