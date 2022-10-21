package com.aiurt.modules.personnelgroupstatistics.model;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lkj
 * @Title:
 * @Description: 班组人员信息
 * @date 2022/10/09 10:56
 */
@Data
public class TeamUserModel {

    private String userId;

    @ApiModelProperty(value = "姓名")
    private String realname;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "工号，唯一键")
    private String workNo;

    @ApiModelProperty(value = " 部门code(所属部门)")
    private String orgCode;

    @ApiModelProperty(value = " 部门name(所属部门)")
    private String orgName;

    @ApiModelProperty(value = "岗位")
    private String jobName;

    @ApiModelProperty(value = "岗位职级：1初级、2中级、3高级、4上岗及以下")
    @Dict(dicCode = "job_grade")
    private Integer jobGrade;

    @ApiModelProperty("检修总工时")
    private String inspecitonTotalTime;

    @ApiModelProperty("巡检总工时")
    private String patrolTotalTime;

    @ApiModelProperty("维修总工时")
    private String faultTotalTime;

    @ApiModelProperty("平均维修响应时间")
    private String averageTime;

    @ApiModelProperty("平均维修时间")
    private String averageFaultTime;

    @ApiModelProperty("月均漏检次数")
    private String averageMonthlyResidual;
}
