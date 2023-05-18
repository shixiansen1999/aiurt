package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author LKJ
 */
@Data
public class MacDto {


    @ApiModelProperty(value = "站点mac地址")
    private List<String> stationMac;

    @ApiModelProperty(value = "当前mac地址")
    private String localMac;


}
