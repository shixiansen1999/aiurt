package com.aiurt.boot.statistics.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ScheduleTask implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private java.lang.String name;
    /**
     * 任务编号
     */
    @ApiModelProperty(value = "任务编号")
    private java.lang.String code;
    /**
     * 任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成
     */
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成")
    private java.lang.Integer status;
    /**
     * 巡视人
     */
    @ApiModelProperty(value = "巡视人")
    private String userInfo;
    /**
     * 站点
     */
    @ApiModelProperty(value = "站点")
    private String stationInfo;
    /**
     * 组织机构
     */
    @ApiModelProperty(value = "组织机构")
    private String orgInfo;
    /**
     * 任务计划执行日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "任务计划执行日期(yyyy-MM-dd)")
    private java.util.Date patrolDate;
    /**
     * 任务提交时间，需要审核的任务以审核时间为准
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务提交时间,返回格式yyyy-MM-dd HH:mm:ss")
    private java.util.Date submitTime;
}
