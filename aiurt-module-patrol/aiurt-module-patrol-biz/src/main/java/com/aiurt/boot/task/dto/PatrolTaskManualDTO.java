package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/4
 * @desc
 */
@Data
public class PatrolTaskManualDTO {

    @Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
    @TableField(value = "`code`")
    private java.lang.String code;
    /*** 任务名称*/
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String name;
    /*** 任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完*/
    @Excel(name = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成", width = 15)
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成")
    private java.lang.Integer status;
    /*** 任务计划执行日期*/
    @Excel(name = "任务计划执行日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "任务计划执行日期")
    private java.util.Date patrolDate;
    /*** 备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /*** 是否需要审核：0否、1是*/
    @Excel(name = "是否需要审核：0否、1是", width = 15)
    @ApiModelProperty(value = "是否需要审核：0否、1是")
    private java.lang.Integer auditor;
    @ApiModelProperty(value = "巡检标准集合")
    @TableField(exist = false)
    List<PatrolTaskStandardDTO> patrolStandardList;
    /**开始时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private Date startTime;
    /**结束时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private Date endTime;
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "多选站点集合")
    @TableField(exist = false)
    List<String> stationCodeList;
    @Excel(name = "组织名称", width = 15)
    @ApiModelProperty(value = "多选组织集合")
    @TableField(exist = false)
    List<String> orgCodeList;
}
