package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author zwl
 */
@Data
public class MaintenanceNameDTO {
    /**班组编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "班组编码")
    private String orgCode;


    /**计划检修总数*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务总数")
    private Long taskTotal;


    /**线路*/
    @TableField(exist = false)
    @ApiModelProperty(value = "线路编码")
    private String lineCode;


    /**站点*/
    @TableField(exist = false)
    @ApiModelProperty(value = "站点编码")
    private String stationCode;


    /**子系统编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "子系统编码")
    private String subsystemCode;

    /**状态*/
    @TableField(exist = false)
    @ApiModelProperty(value = "状态")
    private Long  status;

    /**用户id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "用户id")
    private Long  userId;

    /**姓名*/
    @TableField(exist = false)
    @ApiModelProperty(value = "姓名")
    private String  userName;


    /**开始时间*/
    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    @Excel(name = "开始时间，精确到分钟", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Data startDate;


    /**结束时间*/
    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    @Excel(name = "结束时间，精确到分钟", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Data endDate;
}
