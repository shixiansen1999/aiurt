package com.aiurt.boot.index.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wgp
 * @Title:
 * @Description: 班组人员信息
 * @date 2022/9/1410:10
 */
@Data
public class TeamUserDTO {

    private String userId;

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
    private Long inspecitonTotalTime;

    @ApiModelProperty("巡检总工时")
    private BigDecimal patrolTotalTime;

    @ApiModelProperty("维修总工时")
    private BigDecimal faultTotalTime;

    @ApiModelProperty(value = "参加工作时间")
    @Excel(name = "参加工作时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workingTime;
}
