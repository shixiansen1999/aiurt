package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

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
    /**检查时间*/
    @Excel(name = "检查时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "检查时间")
    private java.util.Date checkTime;
    /**检查结果：0正常、1异常*/
    @Excel(name = "检查结果：0正常、1异常", width = 15)
    @ApiModelProperty(value = "检查结果：0正常、1异常")
    private java.lang.Integer checkResult;
    /**检查用户ID*/
    @Excel(name = "检查用户ID", width = 15)
    @ApiModelProperty(value = "检查用户ID")
    private java.lang.String userId;
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
}
