package com.aiurt.boot.plan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 计划令DTO
 * @date 2022/6/2714:19
 */
@Data
public class PlanCodeDTO {
    @ApiModelProperty("计划令code")
    private String planCode;
    @ApiModelProperty("计划令名称")
    private String planCodeName;
}
