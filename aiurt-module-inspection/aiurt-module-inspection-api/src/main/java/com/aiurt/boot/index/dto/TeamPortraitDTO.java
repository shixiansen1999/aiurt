package com.aiurt.boot.index.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wgp
 * @Title:
 * @Description: 大屏班组画像
 * @date 2022/9/149:51
 */
@Data
public class TeamPortraitDTO {
    @ApiModelProperty("班组id")
    private String teamId;
    @ApiModelProperty("班组code")
    private String teamCode;
    @ApiModelProperty("班组名称")
    private String teamName;
    @ApiModelProperty("班组组长名称")
    private String teamLeaderName;
    @ApiModelProperty("班组负责线路")
    private String teamLineName;
    @ApiModelProperty("站点个数")
    private Integer stationNum;
    @ApiModelProperty("工区位置")
    private String position;
    @ApiModelProperty("当前值班人员")
    private String staffOnDuty;
    @ApiModelProperty("班组平均维修响应时间")
    private Integer averageTime;
    @ApiModelProperty("检修总工时")
    private BigDecimal inspecitonTotalTime;
    /**巡检总工时，单位秒*/
    @ApiModelProperty("巡检总工时")
    private Integer patrolTotalTime;
    @ApiModelProperty("维修总工时")
    private BigDecimal faultTotalTime;

    private String parentId;
    @ApiModelProperty("工区名称")
    private String siteName;
    @ApiModelProperty("工区code")
    private String workAreaCode;
    @ApiModelProperty("工区位置")
    private String positionName;
    @ApiModelProperty("管辖范围")
    private String jurisdiction;
}
