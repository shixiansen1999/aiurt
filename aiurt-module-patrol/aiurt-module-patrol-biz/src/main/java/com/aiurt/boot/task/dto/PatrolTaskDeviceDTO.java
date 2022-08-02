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
    /**专业code*/
    @Excel(name = "专业code", width = 15)
    @ApiModelProperty(value = "专业code")
    private java.lang.String professionCode;
    /**适用系统code*/
    @Excel(name = "适用系统code", width = 15)
    @ApiModelProperty(value = "适用系统code")
    private java.lang.String subsystemCode;
    /**站点*/
    @Excel(name = "站点", width = 15)
    @ApiModelProperty(value = "站点")
    @TableField(exist = false)
    private java.lang.String stationName;
    /**组织code*/
    @Excel(name = "组织code", width = 15)
    @ApiModelProperty(value = "组织code")
    @TableField(exist = false)
    private List <String> orgList;
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
    @Excel(name = "结束巡检时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "结束巡检时间")
    private java.util.Date checkTime;
    @Excel(name = "巡检时长", width = 15)
    @ApiModelProperty(value = "巡检时长")
    @TableField(exist = false)
    private Long inspectionTime;
    /**备注说明*/
    @Excel(name = "备注说明", width = 15)
    @ApiModelProperty(value = "备注说明")
    private java.lang.String remark;
    /**巡检结果：0正常、1异常*/
    @Excel(name = "巡检结果：0正常、1异常", width = 15)
    @ApiModelProperty(value = "巡检结果：0正常、1异常")
    private String checkResult;
    /**工单提交用户ID*/
    @Excel(name = "工单提交用户ID", width = 15)
    @ApiModelProperty(value = "工单提交用户ID")
    private java.lang.String userId;
    /**工单提交用户名称*/
    @Excel(name = "提交人", width = 15)
    @ApiModelProperty(value = "提交人")
    private java.lang.String submitName;
    /**巡检状态：0未开始、1巡检中、2已完成*/
    @Excel(name = "巡检状态：0未开始、1进行中、2已提交", width = 15)
    @ApiModelProperty(value = "巡检状态：0未开始、1进行中、2已提交")
    private java.lang.Integer status;
    /**删除状态： 0未删除 1已删除*/
    @Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
    @Excel(name = "巡检标准表Id", width = 15)
    @ApiModelProperty(value = "巡检标准表Id")
    private java.lang.String standardId;
    /**与设备类型相关：0否 1 是*/
    @Excel(name = "与设备类型相关：0否 1 是", width = 15)
    @ApiModelProperty(value = "与设备类型相关：0否 1 是")
    @TableField(exist = false)
    private java.lang.Integer deviceType;
    @Excel(name = "同行人", width = 15)
    @ApiModelProperty(value = "同行人")
    @TableField(exist = false)
    private String userName;
    @Excel(name = "同行人的信息", width = 15)
    @ApiModelProperty(value = "同行人的信息")
    private List <PatrolAccompanyDTO> accompanyName;
    /**巡检位置*/
    @Excel(name = "巡检位置", width = 15)
    @ApiModelProperty(value = "巡检位置")
    @TableField(exist = false)
    private java.lang.String  inspectionPosition;
    /**巡检自定义位置*/
    @Excel(name = "巡检自定义位置", width = 15)
    @ApiModelProperty(value = "巡检自定义位置")
    private java.lang.String customPosition;
    /**正常项*/
    @Excel(name = "正常项", width = 15)
    @ApiModelProperty(value = "正常项")
    private java.lang.Integer rightCheckNumber;
    /**异常项*/
    @Excel(name = "异常项", width = 15)
    @ApiModelProperty(value = "异常项")
    private java.lang.Integer aberrantNumber;
    /***附件*/
    @ApiModelProperty(value = "附件")
    private List<PatrolAccessoryDTO> accessoryDTOList;
    /***工单查看详情*/
    @ApiModelProperty(value = "工单查看详情（传1标识）")
    private Integer checkDetail;
    /**线路编号*/
    @Excel(name = "线路编号", width = 15)
    @ApiModelProperty(value = "线路编号")
    private java.lang.String lineCode;
    /**站点编号*/
    @Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    private java.lang.String stationCode;
    /**位置编号*/
    @Excel(name = "位置编号", width = 15)
    @ApiModelProperty(value = "位置编号")
    private java.lang.String positionCode;
    /**位置选择*/
    @Excel(name = "位置选择", width = 15)
    @ApiModelProperty(value = "位置选择")
    private List <String> allPosition;
}
