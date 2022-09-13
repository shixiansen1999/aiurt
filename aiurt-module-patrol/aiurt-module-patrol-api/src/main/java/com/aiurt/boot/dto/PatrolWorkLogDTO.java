package com.aiurt.boot.dto;

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
public class PatrolWorkLogDTO {
    /**巡检检查表Id*/
    @Excel(name = "巡检检查表Id", width = 15)
    @ApiModelProperty(value = "巡检检查表Id")
    private String patrolTaskTableId;
    /**巡检检查表*/
    @Excel(name = "巡检检查表", width = 15)
    @ApiModelProperty(value = "巡检检查表")
    private String patrolTaskTable;
    /**站点*/
    @Excel(name = "站点", width = 15)
    @ApiModelProperty(value = "站点")
    private String station;
    /**巡检人*/
    @Excel(name = "巡检人", width = 15)
    @ApiModelProperty(value = "巡检人")
    private String name;
}
