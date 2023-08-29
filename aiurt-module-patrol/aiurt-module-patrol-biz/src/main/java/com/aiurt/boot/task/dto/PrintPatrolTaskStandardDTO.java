package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author qkx
 */
@Data
public class PrintPatrolTaskStandardDTO {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "巡视工单")
    private List<PrintStandardDetailDTO> billInfo;


}
