package com.aiurt.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author WangHongTao
 * @Date 2021/11/19
 */
@Data
public class FaultMonthResult {


    @ApiModelProperty(value = "月份")
    private Integer thisMonth;

    @ApiModelProperty(value = "总数")
    private Integer sumNum;

    @ApiModelProperty(value = "自检数量")
    private Integer selfCheckNum;

    @ApiModelProperty(value = "报修数量")
    private Integer repairNum;

    @ApiModelProperty(value = "去年同期数量")
    private Integer lastYearNum;

}
