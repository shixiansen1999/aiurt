package com.aiurt.modules.personnelgroupstatistics.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

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

    @Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private String realname;

    @Excel(name = "检修总工时", width = 15,groupName = "施工总工时（不含施工）")
    @ApiModelProperty("检修总工时")
    private String inspecitonTotalTime;

    @Excel(name = "巡检总工时", width = 15,groupName = "施工总工时（不含施工）")
    @ApiModelProperty("巡检总工时")
    private String patrolTotalTime;

    @Excel(name = "维修总工时", width = 15,groupName = "施工总工时（不含施工）")
    @ApiModelProperty("维修总工时")
    private String faultTotalTime;

    @Excel(name = "巡视计划任务数", width = 15,groupName = "计划任务数")
    @ApiModelProperty("巡视计划任务数")
    private String patrolScheduledTasks;

    @Excel(name = "检修计划任务数", width = 15,groupName = "计划任务数")
    @ApiModelProperty("检修计划任务数")
    private String inspecitonScheduledTasks;

    @Excel(name = "巡视实际完成任务数", width = 15,groupName = "实际完成任务数")
    @ApiModelProperty("巡视实际完成任务数")
    private String patrolCompletedTasks;

    @Excel(name = "检修实际完成任务数", width = 15,groupName = "实际完成任务数")
    @ApiModelProperty("检修实际完成任务数")
    private String inspecitonCompletedTasks;

    @Excel(name = "巡视计划完成率", width = 15,groupName = "计划完成率")
    @ApiModelProperty("巡视计划完成率")
    private String patrolPlanCompletion;

    @Excel(name = "检修计划完成率", width = 15,groupName = "计划完成率")
    @ApiModelProperty("检修计划完成率")
    private String inspecitonPlanCompletion;

    @Excel(name = "巡视漏检数", width = 15,groupName = "漏检数")
    @ApiModelProperty("巡视漏检数")
    private String patrolMissingChecks;

    @Excel(name = "检修漏检数", width = 15,groupName = "漏检数")
    @ApiModelProperty("检修漏检数")
    private String inspecitonMissingChecks;

    @Excel(name = "配合施工人次", width = 15)
    @ApiModelProperty(value = "配合施工人次")
    private String assortNum;

    @Excel(name = "配合施工工时", width = 15)
    @ApiModelProperty("配合施工工时")
    private String assortTime;

    @Excel(name = "应急处置次数", width = 15)
    @ApiModelProperty("应急处置次数")
    private String emergencyResponseNum;

    @Excel(name = "应急处置工时", width = 15)
    @ApiModelProperty("应急处置工时")
    private String emergencyHandlingHours;

    @Excel(name = "培训完成次数", width = 15)
    @ApiModelProperty("培训完成次数")
    private String trainFinish;
}
