package com.aiurt.modules.personnelgroupstatistics.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lkj
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="人员班组统计报表信息", description="人员班组统计报表信息")
public class PersonnelGroupModel implements Serializable {

    @ApiModelProperty("班组id")
    private String teamId;
    @ApiModelProperty("班组code")
    private String teamCode;
    @ApiModelProperty("班组名称")
    private String teamName;
    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "姓名")
    private String realname;
    @ApiModelProperty("检修总工时")
    private BigDecimal inspecitonTotalTime;
    @ApiModelProperty("巡检总工时")
    private BigDecimal patrolTotalTime;
    @ApiModelProperty("维修总工时")
    private BigDecimal faultTotalTime;

    @ApiModelProperty("巡视计划任务数")
    private BigDecimal patrolScheduledTasks;
    @ApiModelProperty("巡视实际完成任务数")
    private BigDecimal patrolCompletedTasks;
    @ApiModelProperty("检修计划任务数")
    private BigDecimal inspecitonScheduledTasks;
    @ApiModelProperty("检修实际完成任务数")
    private BigDecimal inspecitonCompletedTasks;

    @ApiModelProperty("巡视计划完成率")
    private BigDecimal patrolPlanCompletion;
    @ApiModelProperty("检修计划完成率")
    private BigDecimal inspecitonPlanCompletion;
    @ApiModelProperty("巡视漏检数")
    private BigDecimal patrolMissingChecks;
    @ApiModelProperty("检修漏检数")
    private BigDecimal inspecitonMissingChecks;

    @ApiModelProperty(value = "配合施工人次")
    private Integer assortNum;
    @ApiModelProperty("配合施工工时")
    private BigDecimal assortTime;
    @ApiModelProperty("应急处置次数")
    private BigDecimal emergencyResponseNum;
    @ApiModelProperty("应急处置工时")
    private BigDecimal emergencyHandlingHours;
    @ApiModelProperty("培训完成次数")
    private BigDecimal trainFinish;
}
