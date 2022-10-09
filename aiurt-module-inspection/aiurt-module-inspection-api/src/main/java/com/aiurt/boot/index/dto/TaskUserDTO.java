package com.aiurt.boot.index.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskUserDTO {

    private String taskId;

    @ApiModelProperty("检修工时")
    private BigDecimal inspecitonTotalTime;
}
