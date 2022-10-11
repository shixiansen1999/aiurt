package com.aiurt.boot.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/10/9
 * @desc
 */
@Data
public class UserTeamPatrolDTO {
    @ApiModelProperty(value = "用户Id")
    private String userId;
    @ApiModelProperty(value = "任务Id")
    private String taskId;
    @ApiModelProperty(value = "班组Id")
    private String orgId;
    @ApiModelProperty(value = "巡视工时")
    private BigDecimal workHours;
    @ApiModelProperty(value = "计划任务数")
    private Integer planTaskNumber;
    @ApiModelProperty(value = "实际完成任务数")
    private Integer actualFinishTaskNumber;
    @ApiModelProperty(value = "计划完成率")
    private BigDecimal planFinishRate;
    @ApiModelProperty(value = "漏巡数")
    private Integer missPatrolNumber;
    @ApiModelProperty(value = "平均每月漏检数")
    private BigDecimal avgMissPatrolNumber;

}
