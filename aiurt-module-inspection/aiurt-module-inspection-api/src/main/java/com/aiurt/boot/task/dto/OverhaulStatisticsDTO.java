package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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


}
