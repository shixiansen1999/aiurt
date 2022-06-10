package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renanfeng
 * @version 1.0
 * @date 2022/01/25 13:30
 */
@Data
public class StatisticsFaultLevelVO {

    @ApiModelProperty(value = "级别")
    private String level;
    @ApiModelProperty(value = "时长")
    private int duration;
    @ApiModelProperty(value = "故障编号")
    private String faultCode;
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;
}
