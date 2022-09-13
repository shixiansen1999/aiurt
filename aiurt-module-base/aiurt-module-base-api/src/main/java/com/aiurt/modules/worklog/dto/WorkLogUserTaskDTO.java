package com.aiurt.modules.worklog.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/9/13
 * @desc
 */
@Data
public class WorkLogUserTaskDTO {
    /**巡检内容*/
    @Excel(name = "巡检内容", width = 15)
    @ApiModelProperty(value = "巡检内容")
    private  String  patrolContent;

    /**检修内容*/
    @Excel(name = "检修内容", width = 15)
    @ApiModelProperty(value = "检修内容")
    private  String  repairContent;

    /**故障内容*/
    @Excel(name = "故障内容", width = 15)
    @ApiModelProperty(value = "故障内容")
    private  String  faultContent;
}
