package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author LKJ
 */
@Data
public class PrintStationDTO {

    @ApiModelProperty(value = "巡视地点")
    private String stationName;
    @ApiModelProperty(value = "巡视内容")
    private List<PrintSystemDTO> printSystemDTOS;
}