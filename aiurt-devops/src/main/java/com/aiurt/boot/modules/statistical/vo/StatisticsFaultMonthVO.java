package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renanfeng
 * @version 1.0
 * @date 2022/01/25 13:30
 */
@Data
public class StatisticsFaultMonthVO {

    @ApiModelProperty(value = "月份")
    private String month;
    @ApiModelProperty(value = "数量")
    private int count;
}
