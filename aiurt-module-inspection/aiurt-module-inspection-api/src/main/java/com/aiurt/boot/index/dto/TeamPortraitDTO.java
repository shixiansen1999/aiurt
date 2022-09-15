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
    @ApiModelProperty("班组名称")
    private String teamName;
    @ApiModelProperty("高级资质")
    private String highGrade;
    @ApiModelProperty("中级资质")
    private String middleGrade;
    @ApiModelProperty("初级资质")
    private String juniorGrade;
    @ApiModelProperty("检修总工时")
    private BigDecimal inspecitonTotalTime;
    @ApiModelProperty("巡检总工时")
    private BigDecimal patrolTotalTime;
    @ApiModelProperty("维修总工时")
    private BigDecimal faultTotalTime;
}
