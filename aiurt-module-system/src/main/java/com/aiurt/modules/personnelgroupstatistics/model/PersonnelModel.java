package com.aiurt.modules.personnelgroupstatistics.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author lkj
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="人员统计报表信息", description="人员统计报表信息")
public class PersonnelModel {
    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "姓名")
    private String realname;
    @ApiModelProperty("检修总工时")
    private String inspecitonTotalTime;
    @ApiModelProperty("巡检总工时")
    private String patrolTotalTime;
    @ApiModelProperty("维修总工时")
    private String faultTotalTime;

    @ApiModelProperty("巡视计划任务数")
    private String patrolScheduledTasks;
    @ApiModelProperty("巡视实际完成任务数")
    private String patrolCompletedTasks;
    @ApiModelProperty("检修计划任务数")
    private String inspecitonScheduledTasks;
    @ApiModelProperty("检修实际完成任务数")
    private String inspecitonCompletedTasks;

    @ApiModelProperty("巡视计划完成率")
    private String patrolPlanCompletion;
    @ApiModelProperty("检修计划完成率")
    private String inspecitonPlanCompletion;
    @ApiModelProperty("巡视漏检数")
    private String patrolMissingChecks;
    @ApiModelProperty("检修漏检数")
    private String inspecitonMissingChecks;

    @ApiModelProperty(value = "配合施工人次")
    private String assortNum;
    @ApiModelProperty("配合施工工时")
    private String assortTime;
    @ApiModelProperty("应急处置次数")
    private String emergencyResponseNum;
    @ApiModelProperty("应急处置工时")
    private String emergencyHandlingHours;
    @ApiModelProperty("培训完成次数")
    private String trainFinish;
}
