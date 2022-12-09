package com.aiurt.boot.plan.dto;
/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/6
 * @time: 14:50
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-12-06 14:50
 */
@Data
public class EmergencyPlanTeamDTO {
    @ApiModelProperty(value = "应急队伍id")
    private String emergencyTeamId;
    @ApiModelProperty(value = "应急队伍名称")
    private String emergencyTeamName;
}
