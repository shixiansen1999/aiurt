package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/6
 * @desc
 */
@Data
public class PatrolAccessorySaveDTO {
    /**巡检检查结果表ID*/
    @Excel(name = "巡检检查结果表ID", width = 15)
    @ApiModelProperty(value = "巡检检查结果表ID")
    private java.lang.String id;
    /**巡检任务设备关联表ID*/
    @Excel(name = "巡检任务设备关联表ID", width = 15)
    @ApiModelProperty(value = "巡检任务设备关联表ID")
    private java.lang.String taskDeviceId;
    @ApiModelProperty(value = "附件保存的信息")
    List<PatrolAccessoryDTO> patrolAccessoryDTOList;
}
