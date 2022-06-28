package com.aiurt.boot.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: repair_task_device_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("repair_task_device_rel")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="repair_task_device_rel对象", description="repair_task_device_rel")
public class RepairTaskDeviceRel implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**检修单的单号,示例:JX20211105 */
	@Excel(name = "检修单的单号,示例:JX20211105 ", width = 15)
    @ApiModelProperty(value = "检修单的单号,示例:JX20211105 ")
    private java.lang.String code;
	/**检修任务表ID，关联repair_task的id*/
	@Excel(name = "检修任务表ID，关联repair_task的id", width = 15)
    @ApiModelProperty(value = "检修任务表ID，关联repair_task的id")
    private java.lang.String repairTaskId;
	/**检修任务关联标准表id，关联repair_task_standard_rel的id*/
	@Excel(name = "检修任务关联标准表id，关联repair_task_standard_rel的id", width = 15)
    @ApiModelProperty(value = "检修任务关联标准表id，关联repair_task_standard_rel的id")
    private java.lang.String taskStandardRelId;
	/**设备code，关联device表的code*/
	@Excel(name = "设备code，关联device表的code", width = 15)
    @ApiModelProperty(value = "设备code，关联device表的code")
    private java.lang.String deviceCode;
    /**站所编号*/
    @Excel(name = "站所编号", width = 15)
    @ApiModelProperty(value = "站所编号")
    private java.lang.String stationCode;
    /**线路编号*/
    @Excel(name = "线路编号", width = 15)
    @ApiModelProperty(value = "线路编号")
    private java.lang.String lineCode;
    /**位置编号*/
    @Excel(name = "位置编号", width = 15)
    @ApiModelProperty(value = "位置编号")
    private java.lang.String positionCode;
    @ApiModelProperty(value = "具体位置")
    private java.lang.String specificLocation;
	/**提交人id*/
	@Excel(name = "提交人id", width = 15)
    @ApiModelProperty(value = "提交人id")
    private java.lang.String staffId;
	/**开始时间(yyyy-MM-dd HH:mm)*/
	@Excel(name = "开始时间(yyyy-MM-dd HH:mm)", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm)")
    private java.util.Date startTime;
	/**结束时间(yyyy-MM-dd HH:mm)*/
	@Excel(name = "结束时间(yyyy-MM-dd HH:mm)", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm)")
    private java.util.Date endTime;
	/**回调故障编号*/
	@Excel(name = "回调故障编号", width = 15)
    @ApiModelProperty(value = "回调故障编号")
    private java.lang.String faultCode;
	/**检修时长(单位分钟)*/
	@Excel(name = "检修时长(单位分钟)", width = 15)
    @ApiModelProperty(value = "检修时长(单位分钟)")
    private java.lang.Integer duration;

    /**提交时间(yyyy-MM-dd HH:mm:ss)*/
    @Excel(name = "提交时间(yyyy-MM-dd HH:mm:ss)", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间(yyyy-MM-dd HH:mm:ss)")
    private java.util.Date submitTime;

	/**是否已提交，0未提交1已提交*/
	@Excel(name = "是否已提交，0未提交1已提交", width = 15)
    @ApiModelProperty(value = "是否已提交，0未提交1已提交")
    private java.lang.Integer isSubmit;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
