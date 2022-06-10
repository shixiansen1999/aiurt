package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qian
 * @version 1.0
 * @date 2021/11/20 15:30
 */
@Data
public class StatisticsResultVO {
    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "总检修数")
    private Integer repairAmount;

    @ApiModelProperty(value = "总巡修数")
    private Integer patrolAmount;

    @ApiModelProperty(value = "故障处理数")
    private Integer faultHandleAmount;

    @ApiModelProperty(value = "故障处理时长(分组)")
    private Integer faultHandleTimeAmount;

    @ApiModelProperty(value = "配合施工人次")
    private Integer cooperateBuildAmount;
}
