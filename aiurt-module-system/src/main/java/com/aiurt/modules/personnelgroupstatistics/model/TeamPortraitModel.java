package com.aiurt.modules.personnelgroupstatistics.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lkj
 * @Title:
 * @Description: 班组画像
 * @date 2022/10/09 10:56
 */
@Data
public class TeamPortraitModel {
    @ApiModelProperty("班组id")
    private String teamId;
    @ApiModelProperty("班组code")
    private String teamCode;
    @ApiModelProperty("班组名称")
    private String teamName;
    @ApiModelProperty("班组负责人名称")
    private String teamLeaderName;
    @ApiModelProperty("班组负责人电话")
    private String teamLeaderPhone;
    @ApiModelProperty("班组平均维修响应时间")
    private String averageTime;
    @ApiModelProperty("检修总工时")
    private String inspecitonTotalTime;
    @ApiModelProperty("巡检总工时")
    private String patrolTotalTime;
    @ApiModelProperty("维修总工时")
    private String faultTotalTime;
    @ApiModelProperty("工区位置")
    private String positionName;
    @ApiModelProperty("管辖范围")
    private String jurisdiction;

    @ApiModelProperty("平均维修时间")
    private String averageFaultTime;
    @ApiModelProperty("月均漏检次数")
    private String averageMonthlyResidual;


    private String position;
    @ApiModelProperty("工区名称")
    private String siteName;
    @ApiModelProperty("工区code")
    private String workAreaCode;
}
