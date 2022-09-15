package com.aiurt.boot.index.dto;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wgp
 * @Title:
 * @Description: 班组人员信息
 * @date 2022/9/1410:10
 */
@Data
public class TeamUserDTO {
    @ApiModelProperty(value = "姓名")
    private String realname;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "岗位职级：1初级、2中级、3高级、4上岗及以下")
    @Dict(dicCode = "job_grade")
    private Integer jobGrade;

    @ApiModelProperty(value = "角色")
    private String roleName;

    @ApiModelProperty(value = "工作年限")
    private String workingYears;

    @ApiModelProperty("检修总工时")
    private BigDecimal inspecitonTotalTime;

    @ApiModelProperty("巡检总工时")
    private BigDecimal patrolTotalTime;

    @ApiModelProperty("维修总工时")
    private BigDecimal faultTotalTime;
}
