package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renanfeng
 * @version 1.0
 * @date 2022/01/25 13:30
 */
@Data
public class StatisticsRepairVO {
    @ApiModelProperty(value = "组名")
    private String orgName;
    @ApiModelProperty(value = "已检修数")
    private int repairCount;
    @ApiModelProperty(value = "未检修数")
    private int notRepairCount;
}
