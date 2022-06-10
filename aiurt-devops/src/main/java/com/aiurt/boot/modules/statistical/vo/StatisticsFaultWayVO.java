package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renanfeng
 * @version 1.0
 * @date 2022/01/25 13:30
 */
@Data
public class StatisticsFaultWayVO {
    @ApiModelProperty(value = "报修方式")
    private String repairWay;
    @ApiModelProperty(value = "保修数")
    private int count;
}
