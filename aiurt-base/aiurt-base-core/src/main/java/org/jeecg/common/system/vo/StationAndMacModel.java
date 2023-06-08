package org.jeecg.common.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LKJ
 */
@Data
public class StationAndMacModel {
    @ApiModelProperty(value = "站点名称")
    private String stationName;

    @ApiModelProperty(value = "站点mac地址")
    private String mac;
}
