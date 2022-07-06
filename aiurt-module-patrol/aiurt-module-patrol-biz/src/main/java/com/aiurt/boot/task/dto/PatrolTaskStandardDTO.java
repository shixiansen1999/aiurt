package com.aiurt.boot.task.dto;

import com.aiurt.boot.standard.entity.PatrolStandard;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/6/27
 * @desc
 */
@Data
public class PatrolTaskStandardDTO extends PatrolStandard {
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    private java.lang.String majorName;
    @Excel(name = "巡检标准表ID", width = 15)
    @ApiModelProperty(value = "巡检标准表ID")
    private java.lang.String standardId;
    @Excel(name = "巡检任务标准关联表Id", width = 15)
    @ApiModelProperty(value = "巡检任务标准关联表Id")
    private java.lang.String taskStandardId;
    @Excel(name = "子系统名称", width = 15)
    @ApiModelProperty(value = "子系统名称")
    private java.lang.String sysName;
    @Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    private java.lang.String deviceTypeName;
    @Excel(name = "巡检任务ID", width = 15)
    @ApiModelProperty(value = "巡检任务ID")
    private java.lang.String taskId;
    @ApiModelProperty(value = "设备的集合")
    @TableField(exist = false)
    List<DeviceDTO> deviceList;
}

