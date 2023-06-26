package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author zwl
 */
@Data
public class LeadingOutDTO {

    /**
     * 任务名称
     */
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private java.lang.String name;


    /**
     * 任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成
     */
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成")
    @TableField(exist = false)
    private java.lang.Integer status;

    /**
     *任务状态
     */
    @Excel(name = "任务状态", width = 15)
    @ApiModelProperty(value = "任务状态名称")
    @TableField(exist = false)
    private java.lang.String statusName;

    /**
     * 任务编号
     */
    @Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
    @TableField(exist = false)
    private java.lang.String code;


    /**
     * 任务获取方式：1 个人领取、2常规指派、3 手工下发
     */
    @ApiModelProperty(value = "任务获取方式：1 个人领取、2常规指派、3 手工下发")
    @TableField(exist = false)
    private java.lang.Integer source;


    /**
     * 任务获取方式：1 个人领取、2常规指派、3 手工下发
     */
    @Excel(name = "任务获取方式", width = 15)
    @ApiModelProperty(value = "任务获取方式：1 个人领取、2常规指派、3 手工下发")
    @TableField(exist = false)
    private java.lang.String sourceName;


    /**
     * 巡视人
     */
    @Excel(name = "巡视人", width = 15)
    @ApiModelProperty(value = "巡视人")
    @TableField(exist = false)
    private java.lang.String inspectorName;


    /**
     * 任务执行日期
     */
    @Excel(name = "任务执行日期", width = 15)
    @ApiModelProperty(value = "任务执行日期")
    @TableField(exist = false)
    private java.lang.String patrolDate;

    /**
     * 手工下发巡检的开始日期(yyyy-MM-dd)
     */
    @Excel(name = "手工下发巡检的开始日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "手工下发巡检的开始日期(yyyy-MM-dd)")
    private java.util.Date startDate;
    /**
     * 手工下发巡检的结束日期(yyyy-MM-dd)
     */
    @Excel(name = "手工下发巡检的结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "手工下发巡检的结束日期(yyyy-MM-dd)")
    private java.util.Date endDate;
    /**
     * 任务巡视起止时间
     */
    @Excel(name = "任务巡视起止时间", width = 15)
    @ApiModelProperty(value = "任务巡视起止时间")
    @TableField(exist = false)
    private java.lang.String startAndEndTime;



    /**
     * 站点
     */
    @Excel(name = "站点", width = 15)
    @ApiModelProperty(value = "站点")
    @TableField(exist = false)
    private java.lang.String stationName;


    /**
     * 组织机构
     */
    @Excel(name = "组织机构", width = 15)
    @ApiModelProperty(value = "组织机构")
    @TableField(exist = false)
    private java.lang.String departName;

    /**
     * 作废状态：0未作废、1已作废
     */
    @ApiModelProperty(value = "作废状态：0未作废、1已作废")
    @TableField(exist = false)
    private java.lang.Integer discardStatus;


    /**
     * 是否为作废任务
     */
    @Excel(name = "是否为作废任务", width = 15)
    @TableField(exist = false)
    private java.lang.String discardStatusName;


    /**
     * 漏检状态:0未漏检，1已漏检
     */
    @ApiModelProperty(value = "漏检状态:0未漏检，1已漏检")
    @TableField(exist = false)
    private java.lang.Integer omitStatus;


    /**
     * 是否为漏检任务
     */
    @Excel(name = "是否为漏检任务", width = 15)
    @TableField(exist = false)
    private  java.lang.String omitStatusName;
}
