package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renanfeng
 * @version 1.0
 * @date 2022/01/25 13:30
 */
@Data
public class StatisticsPatrolVO {
    @ApiModelProperty(value = "组名")
    private String orgName;
    @ApiModelProperty(value = "已巡检数")
    private int patrolCount;
    @ApiModelProperty(value = "未巡检数")
    private int notPatrolCount;
}
