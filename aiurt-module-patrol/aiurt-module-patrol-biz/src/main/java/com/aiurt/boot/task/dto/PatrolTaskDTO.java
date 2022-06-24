package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @date 2022/6/22
 * @desc
 */
@Data
public class PatrolTaskDTO
{

    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
    /**任务编号*/
    @Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
    private java.lang.String code;
    /**任务名称*/
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String name;
    /*** 计划编号*/
    @Excel(name = "计划编号", width = 15)
    @ApiModelProperty(value = "计划编号")
    private java.lang.String planCode;
    @Excel(name = "巡检的日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "巡检的日期")
    private java.util.Date patrolDate;
    @Excel(name = "组织机构编号", width = 15)
    @ApiModelProperty(value = "组织机构编号")
    private List<String> orgCode;
    @Excel(name = "组织机构名称", width = 15)
    @ApiModelProperty(value = "组织机构名称")
    private String organizationName;
    @Excel(name = "站点id", width = 15)
    @ApiModelProperty(value = "站点id")
    private String stationId;
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    @Excel(name = "巡检人名称", width = 15)
    @ApiModelProperty(value = "巡检人名称")
    private String patrolUserName;
    @Excel(name = "退回人id", width = 15)
    @ApiModelProperty(value = "退回人id")
    private  String backId;
    @Excel(name = "退回人名称", width = 15)
    @ApiModelProperty(value = "退回人名称")
    private String patrolReturnUserName;
    /**
     * 任务获取方式：1 常规分发、2常规指派、3 手工下发
     */
    @Excel(name = "任务获取方式：1 常规分发、2常规指派、3 手工下发", width = 15)
    @ApiModelProperty(value = "任务获取方式：1 常规分发、2常规指派、3 手工下发")
    private java.lang.Integer source;
    /**
     * 是否需要审核：0否、1是
     */
    @Excel(name = "是否需要审核：0否、1是", width = 15)
    @ApiModelProperty(value = "是否需要审核：0否、1是")
    private java.lang.Integer auditor;
    /**
     * 巡检开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
    @DateTimeFormat(pattern = "HH:mm:ss")
    @ApiModelProperty(value = "巡检开始时间")
    private java.util.Date startTime;
    /**
     * 巡检结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
    @DateTimeFormat(pattern = "HH:mm:ss")
    @ApiModelProperty(value = "巡检结束时间")
    private java.util.Date endTime;
    /**
     * 巡检频次：1 一天1次、2 一周1次、3 一周2次
     */
    @Excel(name = "巡检频次：1 一天1次、2 一周1次、3 一周2次", width = 15)
    @ApiModelProperty(value = "巡检频次：1 一天1次、2 一周1次、3 一周2次")
    private java.lang.Integer period;
    /**
     * 任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5待审核、6已完成
     */
    @Excel(name = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5待审核、6已完成", width = 15)
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5待审核、6已完成")
    private java.lang.Integer status;
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
}

