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
public class OverhaulStatisticsDTO {


    /**班组编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "班组编码")
    private String orgCode;


    /**班组名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "班组名称")
    private String orgName;


    /**姓名*/
    @TableField(exist = false)
    @ApiModelProperty(value = "姓名")
    private String deviceId;

    /**总检修时长*/
    @TableField(exist = false)
    @ApiModelProperty(value = "总检修时长")
    private Long maintenanceDuration;

    /**计划检修总数*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务总数")
    private String taskTotal;


    /**已完成数*/
    @TableField(exist = false)
    @ApiModelProperty(value = "已完成数")
    private Long completedNumber;

    /**未完成数*/
    @TableField(exist = false)
    @ApiModelProperty(value = "未完成数")
    private Long notCompletedNumber;

    /**漏检修数*/
    @TableField(exist = false)
    @ApiModelProperty(value = "漏检修数")
    private Long leakOverhaulNumber;


    /**平均每周漏检修数*/
    @TableField(exist = false)
    @ApiModelProperty(value = "平均每周漏检修数")
    private Long avgWeekNumber;

    /**平均每周漏检修数*/
    @TableField(exist = false)
    @ApiModelProperty(value = "平均每月漏检修数")
    private Long avgMonthNumber;

    /**完成率*/
    @TableField(exist = false)
    @ApiModelProperty(value = "完成率")
    private String completionRate;

    /**异常数量*/
    @TableField(exist = false)
    @ApiModelProperty(value = "异常数量")
    private Long abnormalNumber;

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

    /**用户id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "用户id")
    private Long  userId;

    /**姓名*/
    @TableField(exist = false)
    @ApiModelProperty(value = "姓名")
    private String  userName;

    /**状态*/
    @TableField(exist = false)
    @ApiModelProperty(value = "状态")
    private Long  status;

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
