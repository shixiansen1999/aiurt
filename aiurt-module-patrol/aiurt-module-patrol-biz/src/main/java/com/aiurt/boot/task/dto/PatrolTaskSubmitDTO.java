package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/6/29
 * @desc
 */
@Data
public class PatrolTaskSubmitDTO {
    @Excel(name = "任务Id", width = 15)
    @ApiModelProperty(value = "任务Id")
    private java.lang.String taskId;
    @Excel(name = "工单总数", width = 15)
    @ApiModelProperty(value = "工单总数")
    private java.lang.Integer totalNumber;
    @Excel(name = "已巡检数", width = 15)
    @ApiModelProperty(value = "已巡检数")
    private java.lang.Integer inspectedNumber;
    @Excel(name = "未巡检数", width = 15)
    @ApiModelProperty(value = "未巡检数")
    private java.lang.Integer notInspectedNumber;
}
