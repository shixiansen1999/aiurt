package com.aiurt.boot.task.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author zwl
 * @Title:
 * @Description:
 * @date 2022/6/2409:24
 */
@Data
public class RepairTaskDTO {


    /**检修任务id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务id")
    private String taskId;

    /**检修单id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修单id")
    private String deviceId;

    /**检修任务标准id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务标准id")
    private String standardId;

    /**检修结果id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修结果id")
    private String resultId;

    /**检修人id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修人id")
    private String overhaulId;

    /**检修人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修人名称")
    private String overhaulName;

    /**设备编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "设备编码")
    private String equipmentCode;

    /**设备名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "设备名称")
    private String equipmentName;


    /**专业编码*/
    @ApiModelProperty(value = "专业编码")
    @TableField(exist = false)
    private String majorCode;


    /**专业名称*/
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private String majorName;


    /**系统编码*/
    @ApiModelProperty(value = "系统编码")
    @TableField(exist = false)
    private String systemCode;

    /**专业名称*/
    @ApiModelProperty(value = "系统名称")
    @TableField(exist = false)
    private String systemName;


    /**设备类型编码*/
    @ApiModelProperty(value = "设备类型编码")
    @TableField(exist = false)
    private String deviceTypeCode;

    /**设备类型编码*/
    @ApiModelProperty(value = "设备类型编码")
    @TableField(exist = false)
    private String deviceTypeName;

    /**检修结果*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修结果")
    private Integer maintenanceResults;


    /**检修结果名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修结果名称")
    private String maintenanceResultsName;


    /**检修单号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修单号")
    private String overhaulCode;

    /**检修标准名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修标准名称")
    private String overhaulStandardName;


    /**提交时间*/
    @Excel(name = "提交时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    @TableField(exist = false)
    private java.util.Date submitTime;
}
