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
 * @date 2022/6/26
 * @desc
 */
@Data
public class PatrolTaskDeviceDTO {
    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
    /**巡检任务表ID*/
    @Excel(name = "巡检任务表ID", width = 15)
    @ApiModelProperty(value = "巡检任务表ID")
    private java.lang.String taskId;
    /**巡检任务标准关联表ID*/
    @Excel(name = "巡检任务标准关联表ID", width = 15)
    @ApiModelProperty(value = "巡检任务标准关联表ID")
    private java.lang.String taskStandardId;
    /**巡检表名称*/
    @Excel(name = "巡检表名称", width = 15)
    @ApiModelProperty(value = "巡检表名称")
    private java.lang.String taskStandardName;
    /**巡检单号*/
    @Excel(name = "巡检单号", width = 15)
    @ApiModelProperty(value = "巡检单号")
    private java.lang.String patrolNumber;
    /**回调故障单号*/
    @Excel(name = "回调故障单号", width = 15)
    @ApiModelProperty(value = "回调故障单号")
    private java.lang.String faultCode;
    /**设备code*/
    @Excel(name = "设备code", width = 15)
    @ApiModelProperty(value = "设备code")
    private java.lang.String deviceCode;
    /**设备名称*/
    @Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
    private java.lang.String deviceName;
    /**设备位置*/
    @Excel(name = "设备位置", width = 15)
    @ApiModelProperty(value = "设备位置")
    private java.lang.String devicePosition;
    /**开始巡检时间*/
    @Excel(name = "开始巡检时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "开始巡检时间")
    private java.util.Date startTime;
    /**工单提交时间*/
    @Excel(name = "工单提交时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "工单提交时间")
    private java.util.Date checkTime;
    @Excel(name = "巡检时长", width = 15)
    @ApiModelProperty(value = "巡检时长")
    private Long inspectionTime;
    /**检查结果：0正常、1异常*/
    @Excel(name = "检查结果：0正常、1异常", width = 15)
    @ApiModelProperty(value = "检查结果：0正常、1异常")
    private java.lang.Integer checkResult;
    /**工单提交用户ID*/
    @Excel(name = "工单提交用户ID", width = 15)
    @ApiModelProperty(value = "工单提交用户ID")
    private java.lang.String userId;
    /**工单提交用户名称*/
    @Excel(name = "工单提交用户名称", width = 15)
    @ApiModelProperty(value = "工单提交用户名称")
    private java.lang.String submitName;
    /**备注说明*/
    @Excel(name = "备注说明", width = 15)
    @ApiModelProperty(value = "备注说明")
    private java.lang.String remark;
    /**检查状态：0未开始、1巡检中、2已完成*/
    @Excel(name = "检查状态：0未开始、1巡检中、2已完成", width = 15)
    @ApiModelProperty(value = "检查状态：0未开始、1巡检中、2已完成")
    private java.lang.Integer status;
    /**删除状态： 0未删除 1已删除*/
    @Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
    @Excel(name = "巡检标准表Id", width = 15)
    @ApiModelProperty(value = "巡检标准表Id")
    private java.lang.String standardId;
    @Excel(name = "同行人", width = 15)
    @ApiModelProperty(value = "同行人")
    private java.lang.String accompanyName;
    /**巡检位置*/
    @Excel(name = "巡检位置", width = 15)
    @ApiModelProperty(value = "巡位置")
    private java.lang.String  position;
    /**巡检自定义位置*/
    @Excel(name = "巡检自定义位置", width = 15)
    @ApiModelProperty(value = "巡检自定义位置")
    private java.lang.String customPosition;
    /**正常数量*/
    @Excel(name = "正常数量", width = 15)
    @ApiModelProperty(value = "正常数量")
    private java.lang.Integer rightCheckNumber;
    /**异常数量*/
    @Excel(name = "异常数量", width = 15)
    @ApiModelProperty(value = "异常数量")
    private java.lang.Integer aberrantNumber;
    /*** 附件信息*/
    @ApiModelProperty(value = "附件信息")
    private List<PatrolAccessoryDTO> accessoryDTOList;
}
