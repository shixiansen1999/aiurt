package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author qkx
 */
@Data
public class PrintStandardDTO {

    @ApiModelProperty(value = "巡视工单名称")
    private String standardName;
    @ApiModelProperty(value = "巡视内容")
    private List<PrintTaskStationDTO> patrolStationDTOS;
}
