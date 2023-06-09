package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.system.vo.StationAndMacModel;

import java.util.List;

/**
 * @author LKJ
 */
@Data
public class MacDto {


    @ApiModelProperty(value = "站点mac地址")
    private List<StationAndMacModel> stationMac;

    @ApiModelProperty(value = "当前mac地址")
    private List<String> localMac;

    @ApiModelProperty(value = "异常工单")
    private List<String> errorMac;
}
