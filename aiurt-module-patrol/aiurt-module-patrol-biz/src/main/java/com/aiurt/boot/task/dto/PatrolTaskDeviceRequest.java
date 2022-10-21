package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/10/17
 * @desc
 */
@Data
public class PatrolTaskDeviceRequest {
    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String taskDeviceId;
    /**巡检任务表ID*/
    @Excel(name = "巡检任务表ID", width = 15)
    @ApiModelProperty(value = "巡检任务表ID")
    private java.lang.String taskId;
    /**巡检任务标准关联表ID*/
    @Excel(name = "巡检任务表ID", width = 15)
    @ApiModelProperty(value = "巡检任务表ID")
    private java.lang.String userId;
    /**巡检任务标准关联表ID*/
}
