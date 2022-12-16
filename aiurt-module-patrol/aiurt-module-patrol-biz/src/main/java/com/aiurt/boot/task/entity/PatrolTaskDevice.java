package com.aiurt.boot.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("patrol_task_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_task_device对象", description="patrol_task_device")
public class PatrolTaskDevice implements Serializable {
    private static final long serialVersionUID = 1L;

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
    /**开始巡检时间*/
    @Excel(name = "开始巡检时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始巡检时间")
    private java.util.Date startTime;
	/**工单提交时间*/
	@Excel(name = "工单提交时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "工单提交时间")
    private java.util.Date checkTime;
	/**检查结果：0正常、1异常*/
	@Excel(name = "检查结果：0正常、1异常", width = 15)
    @ApiModelProperty(value = "检查结果：0正常、1异常")
    private java.lang.Integer checkResult;
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
    /**巡检自定义位置*/
    @Excel(name = "巡检自定义位置", width = 15)
    @ApiModelProperty(value = "巡检自定义位置")
    private java.lang.String customPosition;
	/**工单提交用户ID*/
	@Excel(name = "工单提交用户ID", width = 15)
    @ApiModelProperty(value = "工单提交用户ID")
    private java.lang.String userId;
	/**备注说明*/
	@Excel(name = "备注说明", width = 15)
    @ApiModelProperty(value = "备注说明")
    private java.lang.String remark;
	/**工单状态：0未开始、1进行中、2已提交*/
	@Excel(name = "工单状态：0未开始、1进行中、2已提交", width = 15)
    @ApiModelProperty(value = "工单状态：0未开始、1进行中、2已提交")
    private java.lang.Integer status;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;

    @ApiModelProperty(value = "巡检任务编号")
    @TableField(exist = false)
    private java.lang.String taskCode;

}
