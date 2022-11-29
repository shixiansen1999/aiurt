package com.aiurt.boot.rehearsal.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: emergency_training_program
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_training_program")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_training_program对象", description="emergency_training_program")
public class EmergencyTrainingProgram implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
	/**训练计划编号*/
	@Excel(name = "训练计划编号", width = 15)
    @ApiModelProperty(value = "训练计划编号")
    private java.lang.String trainingProgramCode;
	/**训练项目名称*/
	@Excel(name = "训练项目名称", width = 15)
    @ApiModelProperty(value = "训练项目名称")
    private java.lang.String trainingProgramName;
	/**训练负责人id*/
	@Excel(name = "训练负责人id", width = 15)
    @ApiModelProperty(value = "训练负责人id")
    private java.lang.String trainingManagerId;
	/**计划训练时间*/
	@Excel(name = "计划训练时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "计划训练时间")
    private java.util.Date trainingPlantime;
	/**计划训练人数*/
	@Excel(name = "计划训练人数", width = 15)
    @ApiModelProperty(value = "计划训练人数")
    private java.lang.Integer traineesNum;
	/**审核状态（1待提交、2待完成、3已完成）*/
	@Excel(name = "审核状态（1待提交、2待完成、3已完成）", width = 15)
    @ApiModelProperty(value = "审核状态（1待提交、2待完成、3已完成）")
    private java.lang.Integer status;
	/**编制部门*/
	@Excel(name = "编制部门", width = 15)
    @ApiModelProperty(value = "编制部门")
    private java.lang.String orgCode;
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
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
