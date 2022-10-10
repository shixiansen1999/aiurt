package com.aiurt.boot.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    @ApiModelProperty(value = "巡视工时")
    private Integer workHours;
    @ApiModelProperty(value = "计划任务数")
    private float planTaskNumber;
    @ApiModelProperty(value = "实际完成任务数")
    private float actualFinishTaskNumber;
    @ApiModelProperty(value = "计划完成率")
    private float planFinishRate;
    @ApiModelProperty(value = "漏巡数")
    private Integer missPatrolNumber;

}
