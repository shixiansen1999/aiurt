package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/4
 * @desc
 */
@Data
public class PatrolTaskManualDTO {

    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
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
    /**
     * 作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3
     */
    @Excel(name = "作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3", width = 15)
    @ApiModelProperty(value = "作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3")
    private java.lang.Integer type;
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
    /*** 巡检开始时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "巡检开始时间")
    private java.util.Date startTime;
    /*** 巡检结束时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "巡检结束时间")
    private java.util.Date endTime;
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "多选站点集合")
    @TableField(exist = false)
    List<String> stationCodeList;
    @Excel(name = "组织名称", width = 15)
    @ApiModelProperty(value = "多选组织集合")
    @TableField(exist = false)
    List<String> orgCodeList;
}
