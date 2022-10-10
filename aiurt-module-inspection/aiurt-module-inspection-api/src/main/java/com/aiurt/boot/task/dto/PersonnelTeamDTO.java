package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zwl
 */
@Data
public class PersonnelTeamDTO {


    /**id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "id")
    private String id;

    /**班组id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "班组id")
    private String  teamId;

    /**人员id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "人员id")
    private String  userId;

    /**开始时间*/
    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    private java.util.Date  startDate;

    /**结束时间*/
    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    private java.util.Date  endDate;


    /**检修总工时*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修总工时")
    private Long  overhaulWorkingHours;

    /**检修计划任务数*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修计划任务数")
    private Long  planTaskNumber;


    /**检修完成任务数*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修完成任务数")
    private Long  completeTaskNumber;


    /**检修计划完成率*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修计划完成率")
    private String  planCompletionRate;


    /**漏检数*/
    @TableField(exist = false)
    @ApiModelProperty(value = "漏检数")
    private Long  undetectedNumber;


    /**任务id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "任务id")
    private String taskId;


    /**任务状态*/
    @TableField(exist = false)
    @ApiModelProperty(value = "任务状态")
    private Integer taskStatus;


    /**计数器*/
    @TableField(exist = false)
    @ApiModelProperty(value = "计数器")
    private Long counter;

}
