package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @Excel(name = "组织机构编码", width = 15)
    @ApiModelProperty(value = "组织机构编码")
    private String orgCode;
    @Excel(name = "组织机构code", width = 15)
    @ApiModelProperty(value = "组织机构code")
    private List<String> orgCodeList;
    @Excel(name = "组织机构名称", width = 15)
    @ApiModelProperty(value = "组织机构名称")
    private String organizationName;
    @Excel(name = "站点code", width = 15)
    @ApiModelProperty(value = "站点code")
    private String stationCode;
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "站点名称")
    private List<String> stationCodeList;
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
    /*** 任务获取方式：1 常规分发、2常规指派、3 手工下发*/
    @Excel(name = "任务获取方式：1 常规分发、2常规指派、3 手工下发", width = 15)
    @ApiModelProperty(value = "任务获取方式：1 常规分发、2常规指派、3 手工下发")
    private java.lang.Integer source;
    /*** 是否需要审核：0否、1是*/
    @Excel(name = "是否需要审核：0否、1是", width = 15)
    @ApiModelProperty(value = "是否需要审核：0否、1是")
    private java.lang.Integer auditor;
    /*** 开始和结束时间*/
    @ApiModelProperty(value = "开始和结束时间")
    @TableField(exist = false)
    private String startEndTime;
    @Excel(name = "巡检结果提交时间", width = 15)
    @ApiModelProperty(value = "巡检结果提交时间")
    private String submitTime;
    /*** 任务提交人 */
    @Excel(name = "任务提交人 ", width = 15)
    @ApiModelProperty(value = "任务提交人 ")
    private java.lang.String endUserName;
    /*** 备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /*** 任务提交的用户签名图片*/
    @Excel(name = "任务提交的用户签名图片", width = 15)
    @ApiModelProperty(value = "任务提交的用户签名图片")
    private java.lang.String signUrl;
    /*** 巡检频次：1 一天1次、2 一周1次、3 一周2次*/
    @Excel(name = "巡检频次：1 一天1次、2 一周1次、3 一周2次", width = 15)
    @ApiModelProperty(value = "巡检频次：1 一天1次、2 一周1次、3 一周2次")
    private String period;
    /*** 任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成*/
    @Excel(name = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成", width = 15)
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成")
    private java.lang.Integer status;
    /*** 退回理由*/
    @Excel(name = "退回理由", width = 15)
    @ApiModelProperty(value = "退回理由")
    private java.lang.String backReason;
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private java.lang.String majorName;
    @Excel(name = "子系统名称", width = 15)
    @ApiModelProperty(value = "子系统名称")
    @TableField(exist = false)
    private java.lang.String sysName;
    @Excel(name = "作业类型", width = 15)
    @ApiModelProperty(value = "作业类型")
    private java.lang.String type;
    /*** 任务计划执行日期范围*/
    @Excel(name = "任务计划执行日期范围", width = 15)
    @ApiModelProperty(value = "任务计划执行日期范围")
    private String dateScope;
    /**
     * 任务计划执行日期范围开始日期
     */
    @Excel(name = "任务计划执行日期范围开始日期", width = 15)
    @ApiModelProperty(value = "任务计划执行日期范围开始日期")
    private Date dateHead;
    /**
     * 任务计划执行日期范围结束日期
     */
    @Excel(name = "任务计划执行日期范围结束日期", width = 15)
    @ApiModelProperty(value = "任务计划执行日期范围结束日期")
    private Date dateEnd;
}

