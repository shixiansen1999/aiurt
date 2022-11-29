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
 * @Description: emergency_plan_record_problem_measures
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_plan_record_problem_measures")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_plan_record_problem_measures对象", description="emergency_plan_record_problem_measures")
public class EmergencyPlanRecordProblemMeasures implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
	/**应急预案启动记录id*/
	@Excel(name = "应急预案启动记录id", width = 15)
    @ApiModelProperty(value = "应急预案启动记录id")
    private java.lang.String emergencyPlanRecordId;
	/**问题类型*/
	@Excel(name = "问题类型", width = 15)
    @ApiModelProperty(value = "问题类型")
    private java.lang.Integer problemType;
	/**问题描述*/
	@Excel(name = "问题描述", width = 15)
    @ApiModelProperty(value = "问题描述")
    private java.lang.String problemContent;
	/**责任部门*/
	@Excel(name = "责任部门", width = 15)
    @ApiModelProperty(value = "责任部门")
    private java.lang.String orgCode;
	/**负责人id*/
	@Excel(name = "负责人id", width = 15)
    @ApiModelProperty(value = "负责人id")
    private java.lang.String managerId;
	/**解决期限*/
	@Excel(name = "解决期限", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "解决期限")
    private java.util.Date resolveTime;
	/**问题状态（1待处理、2已处理）*/
	@Excel(name = "问题状态（1待处理、2已处理）", width = 15)
    @ApiModelProperty(value = "问题状态（1待处理、2已处理）")
    private java.lang.Integer status;
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
