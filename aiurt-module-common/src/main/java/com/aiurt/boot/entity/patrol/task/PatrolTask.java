package com.aiurt.boot.entity.patrol.task;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("patrol_task")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_task对象", description="patrol_task")
public class PatrolTask implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**任务编号*/
	@Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
    private java.lang.String code;
    /**计划编号*/
    @Excel(name = "计划编号", width = 15)
    @ApiModelProperty(value = "计划编号")
    private java.lang.String planCode;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String name;
	/**作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3*/
	@Excel(name = "作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3", width = 15)
    @ApiModelProperty(value = "作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3")
    private java.lang.Integer type;
	/**是否委外：0否、1是*/
	@Excel(name = "是否委外：0否、1是", width = 15)
    @ApiModelProperty(value = "是否委外：0否、1是")
    private java.lang.Integer outsource;
	/**计划令编号*/
	@Excel(name = "计划令编号", width = 15)
    @ApiModelProperty(value = "计划令编号")
    private java.lang.String planOrderCode;
	/**计划令图片*/
	@Excel(name = "计划令图片", width = 15)
    @ApiModelProperty(value = "计划令图片")
    private java.lang.String planOrderCodeUrl;
	/**线路ID*/
	@Excel(name = "线路ID", width = 15)
    @ApiModelProperty(value = "线路ID")
    private java.lang.String lineId;
	/**适用站点ID*/
	@Excel(name = "适用站点ID", width = 15)
    @ApiModelProperty(value = "适用站点ID")
    private java.lang.String stationId;
	/**组织机构ID*/
	@Excel(name = "组织机构ID", width = 15)
    @ApiModelProperty(value = "组织机构ID")
    private java.lang.String organizationId;
	/**专业ID*/
	@Excel(name = "专业ID", width = 15)
    @ApiModelProperty(value = "专业ID")
    private java.lang.String professionId;
	/**适用系统ID*/
	@Excel(name = "适用系统ID", width = 15)
    @ApiModelProperty(value = "适用系统ID")
    private java.lang.String subsystemId;
	/**设备类型ID*/
	@Excel(name = "设备类型ID", width = 15)
    @ApiModelProperty(value = "设备类型ID")
    private java.lang.String deviceTypeId;
	/**巡检频次：1 一天1次、2 一周1次、3 一周2次*/
	@Excel(name = "巡检频次：1 一天1次、2 一周1次、3 一周2次", width = 15)
    @ApiModelProperty(value = "巡检频次：1 一天1次、2 一周1次、3 一周2次")
    private java.lang.Integer period;
	/**巡检的日期*/
	@Excel(name = "巡检的日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "巡检的日期")
    private java.util.Date patrolDate;
	/**任务获取方式：1 常规分发、2常规指派、3 手工下发*/
	@Excel(name = "任务获取方式：1 常规分发、2常规指派、3 手工下发", width = 15)
    @ApiModelProperty(value = "任务获取方式：1 常规分发、2常规指派、3 手工下发")
    private java.lang.Integer source;
	/**巡检结果提交时间*/
	@Excel(name = "巡检结果提交时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "巡检结果提交时间")
    private java.util.Date submitTime;
	/**任务结束用户ID*/
	@Excel(name = "任务结束用户ID", width = 15)
    @ApiModelProperty(value = "任务结束用户ID")
    private java.lang.String endUserId;
	/**任务提交的用户签名图片*/
	@Excel(name = "任务提交的用户签名图片", width = 15)
    @ApiModelProperty(value = "任务提交的用户签名图片")
    private java.lang.String signUrl;
	/**任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5待审核、6已完成*/
	@Excel(name = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5待审核、6已完成", width = 15)
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5待审核、6已完成")
    private java.lang.Integer status;
	/**是否需要审核：0否、1是*/
	@Excel(name = "是否需要审核：0否、1是", width = 15)
    @ApiModelProperty(value = "是否需要审核：0否、1是")
    private java.lang.Integer auditor;
	/**审核用户ID*/
	@Excel(name = "审核用户ID", width = 15)
    @ApiModelProperty(value = "审核用户ID")
    private java.lang.String auditorId;
	/**异常状态：0异常、1正常*/
	@Excel(name = "异常状态：0异常、1正常", width = 15)
    @ApiModelProperty(value = "异常状态：0异常、1正常")
    private java.lang.Integer abnormalState;
	/**处置状态：0未处置、1已处置*/
	@Excel(name = "处置状态：0未处置、1已处置", width = 15)
    @ApiModelProperty(value = "处置状态：0未处置、1已处置")
    private java.lang.Integer disposeStatus;
	/**处置时间*/
	@Excel(name = "处置时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "处置时间")
    private java.util.Date disposeTime;
	/**处置用户ID*/
	@Excel(name = "处置用户ID", width = 15)
    @ApiModelProperty(value = "处置用户ID")
    private java.lang.String disposeId;
	/**漏检说明*/
	@Excel(name = "漏检说明", width = 15)
    @ApiModelProperty(value = "漏检说明")
    private java.lang.String omitExplain;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
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
