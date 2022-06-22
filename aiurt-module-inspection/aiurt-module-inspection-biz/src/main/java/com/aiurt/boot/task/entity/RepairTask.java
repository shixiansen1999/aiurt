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
 * @Description: repair_task
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("repair_task")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="repair_task对象", description="repair_task")
public class RepairTask implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id,自动递增*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id,自动递增")
    private java.lang.String id;
	/**编号,示例:JX20211105 */
	@Excel(name = "编号,示例:JX20211105 ", width = 15)
    @ApiModelProperty(value = "编号,示例:JX20211105 ")
    private java.lang.String code;
	/**检修周期类型，0周检、1月检、2双月检、3季检、4半年检、5年检*/
	@Excel(name = "检修周期类型，0周检、1月检、2双月检、3季检、4半年检、5年检", width = 15)
    @ApiModelProperty(value = "检修周期类型，0周检、1月检、2双月检、3季检、4半年检、5年检")
    private java.lang.Integer type;
	/**周数*/
	@Excel(name = "周数", width = 15)
    @ApiModelProperty(value = "周数")
    private java.lang.Integer weeks;
	/**计划开始时间，精确到分钟*/
	@Excel(name = "计划开始时间，精确到分钟", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始时间，精确到分钟")
    private java.util.Date startTime;
	/**计划结束时间，精确到分钟*/
	@Excel(name = "计划结束时间，精确到分钟", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "计划结束时间，精确到分钟")
    private java.util.Date endTime;
	/**检修状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7待验收、8已完成*/
	@Excel(name = "检修状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7待验收、8已完成 ", width = 15)
    @ApiModelProperty(value = "检修状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7待验收、8已完成 ")
    private java.lang.Integer status;
	/**原因，不予确认/验收 原因*/
	@Excel(name = "原因，不予确认/验收 原因", width = 15)
    @ApiModelProperty(value = "原因，不予确认/验收 原因")
    private java.lang.String errorContent;
	/**是否需要审核：0否 1是*/
	@Excel(name = "是否需要审核：0否 1是", width = 15)
    @ApiModelProperty(value = "是否需要审核：0否 1是")
    private java.lang.Integer isConfirm;
	/**是否需要验收：0否1是*/
	@Excel(name = "是否需要验收：0否1是", width = 15)
    @ApiModelProperty(value = "是否需要验收：0否1是")
    private java.lang.Integer isReceipt;
	/**指派人id，关联sys_user的id*/
	@Excel(name = "指派人id，关联sys_user的id", width = 15)
    @ApiModelProperty(value = "指派人id，关联sys_user的id")
    private java.lang.String assignUserId;
	/**指派人名称*/
	@Excel(name = "指派人名称", width = 15)
    @ApiModelProperty(value = "指派人名称")
    private java.lang.String assignUserName;
	/**指派时间，精确到秒*/
	@Excel(name = "指派时间，精确到秒", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "指派时间，精确到秒")
    private java.util.Date assignTime;
	/**提交人id，关联sys_user的id*/
	@Excel(name = "提交人id，关联sys_user的id", width = 15)
    @ApiModelProperty(value = "提交人id，关联sys_user的id")
    private java.lang.String submitUserId;
	/**提交人*/
	@Excel(name = "提交人", width = 15)
    @ApiModelProperty(value = "提交人")
    private java.lang.String sumitUserName;
	/**检修人点击开始执行任务的时间*/
	@Excel(name = "检修人点击开始执行任务的时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "检修人点击开始执行任务的时间")
    private java.util.Date beginTime;
	/**提交时间，精确到秒*/
	@Excel(name = "提交时间，精确到秒", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "提交时间，精确到秒")
    private java.util.Date submitTime;
	/**确认人id，关联sys_user的id*/
	@Excel(name = "确认人id，关联sys_user的id", width = 15)
    @ApiModelProperty(value = "确认人id，关联sys_user的id")
    private java.lang.String confirmUserId;
	/**确认人*/
	@Excel(name = "确认人", width = 15)
    @ApiModelProperty(value = "确认人")
    private java.lang.String confirmUserName;
	/**确认时间，精确到秒*/
	@Excel(name = "确认时间，精确到秒", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间，精确到秒")
    private java.util.Date confirmTime;
	/**确认签名url*/
	@Excel(name = "确认签名url", width = 15)
    @ApiModelProperty(value = "确认签名url")
    private java.lang.String confirmUrl;
	/**验收人id，关联sys_user的id*/
	@Excel(name = "验收人id，关联sys_user的id", width = 15)
    @ApiModelProperty(value = "验收人id，关联sys_user的id")
    private java.lang.String receiptUserId;
	/**验收人*/
	@Excel(name = "验收人", width = 15)
    @ApiModelProperty(value = "验收人")
    private java.lang.String receiptUserName;
	/**验收时间，精确到秒*/
	@Excel(name = "验收时间，精确到秒", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "验收时间，精确到秒")
    private java.util.Date receiptTime;
	/**验收签名url*/
	@Excel(name = "验收签名url", width = 15)
    @ApiModelProperty(value = "验收签名url")
    private java.lang.String receiptUrl;
	/**检修计划池id，关联repair_pool的id*/
	@Excel(name = "检修计划池id，关联repair_pool的id", width = 15)
    @ApiModelProperty(value = "检修计划池id，关联repair_pool的id")
    private java.lang.String repairPoolId;
	/**作业类型（A1不用计划令,A2,A3,B1,B2,B3）*/
	@Excel(name = "作业类型（A1不用计划令,A2,A3,B1,B2,B3）", width = 15)
    @ApiModelProperty(value = "作业类型（A1不用计划令,A2,A3,B1,B2,B3）")
    private java.lang.String workType;
	/**计划令编码*/
	@Excel(name = "计划令编码", width = 15)
    @ApiModelProperty(value = "计划令编码")
    private java.lang.String planOrderCode;
	/**计划令图片*/
	@Excel(name = "计划令图片", width = 15)
    @ApiModelProperty(value = "计划令图片")
    private java.lang.String planOrderCodeUrl;
	/**是否委外：0否1是*/
	@Excel(name = "是否委外：0否1是", width = 15)
    @ApiModelProperty(value = "是否委外：0否1是")
    private java.lang.Integer isOutsource;
	/**删除状态：0未删除 1已删除*/
	@Excel(name = "删除状态：0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态：0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建时间，精确到秒*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间，精确到秒")
    private java.util.Date createTime;
	/**创建者*/
    @ApiModelProperty(value = "创建者")
    private java.lang.String createBy;
	/**更新时间，精确到秒*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间，精确到秒")
    private java.util.Date updateTime;
	/**更新者*/
    @ApiModelProperty(value = "更新者")
    private java.lang.String updateBy;
}
