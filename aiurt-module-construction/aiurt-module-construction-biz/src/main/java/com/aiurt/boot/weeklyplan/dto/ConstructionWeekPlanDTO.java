package com.aiurt.boot.weeklyplan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ConstructionWeekPlanDTO", description = "施工周计划")
public class ConstructionWeekPlanDTO {
    @ApiModelProperty(value = "ID")
    private String indocno;

    @ApiModelProperty(value = "星期")
    private String weekday;

    @ApiModelProperty(value = "任务日期")
    private String taskDate;

    @ApiModelProperty(value = "任务人数")
    private String taskStaffNum;

    @ApiModelProperty(value = "任务工时")
    private String taskTime;

    @ApiModelProperty(value = "防护措施")
    private String protectiveMeasure;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ApiModelProperty(value = "任务范围")
    private String taskRange;

    @ApiModelProperty(value = "任务内容")
    private String taskContent;

    @ApiModelProperty(value = "负责人")
    private String chargeStaffName;

    @ApiModelProperty(value = "大型设备")
    private String largeAppliances;

    @ApiModelProperty(value = "线路人员")
    private String lineStaffName;

    @ApiModelProperty(value = "调度人员")
    private String dispatchStaffName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "协助站点")
    private String assistStationName;

    @ApiModelProperty(value = "计划变更")
    private String planChange;

    @ApiModelProperty(value = "性质")
    private String nature;

    @ApiModelProperty(value = "电力需求")
    private String ipowerRequirement;

    @ApiModelProperty(value = "供电需求")
    private String powerSupplyRequirement;

    @ApiModelProperty(value = "一级站点")
    private String firstStationName;

    @ApiModelProperty(value = "二级站点")
    private String secondStationName;

    @ApiModelProperty(value = "变电站名称")
    private String substationName;

    @ApiModelProperty(value = "申请人")
    private String applyStaffName;

    @ApiModelProperty(value = "表单状态")
    private String formStatus;

    @ApiModelProperty(value = "申请表单状态")
    private String applyFormStatus;

    @ApiModelProperty(value = "线路表单状态")
    private String lineFormStatus;

    @ApiModelProperty(value = "调度表单状态")
    private String dispatchFormStatus;

    @ApiModelProperty(value = "计划类型")
    private String plantype;

    @ApiModelProperty(value = "计划编号")
    private String planno;

}