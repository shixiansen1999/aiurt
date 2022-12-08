package com.aiurt.boot.plan.entity;

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
    private String id;
	/**应急预案启动记录id*/
	@Excel(name = "应急预案启动记录id", width = 15)
    @ApiModelProperty(value = "应急预案启动记录id")
    private String emergencyPlanRecordId;
	/**问题类型*/
	@Excel(name = "问题类型", width = 15)
    @ApiModelProperty(value = "问题类型")
    private Integer problemType;
	/**问题描述*/
	@Excel(name = "问题描述", width = 15)
    @ApiModelProperty(value = "问题描述")
    private String problemContent;
	/**责任部门*/
	@Excel(name = "责任部门", width = 15)
    @ApiModelProperty(value = "责任部门")
    private String orgCode;

    /**责任部门名称*/
    @Excel(name = "责任部门名称", width = 15)
    @ApiModelProperty(value = "责任部门名称")
    @TableField(exist = false)
    private java.lang.String orgName;

    /**责任部门负责人ID*/
    @Excel(name = "责任部门负责人ID", width = 15)
    @ApiModelProperty(value = "责任部门负责人ID")
    private java.lang.String orgUserId;
    /**责任部门责任人名称*/
    @Excel(name = "责任部门责任人名称", width = 15)
    @ApiModelProperty(value = "责任部门责任人名称")
    @TableField(exist = false)
    private java.lang.String orgUserName;

	/**负责人id*/
	@Excel(name = "负责人id", width = 15)
    @ApiModelProperty(value = "负责人id")
    private String managerId;

    /**责任人名称*/
    @Excel(name = "责任人名称", width = 15)
    @ApiModelProperty(value = "责任人名称")
    @TableField(exist = false)
    private java.lang.String userName;

	/**解决期限*/
	@Excel(name = "解决期限", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "解决期限")
    private java.util.Date resolveTime;
	/**问题状态（1待处理、2已处理）*/
	@Excel(name = "问题状态（1待处理、2已处理）", width = 15)
    @ApiModelProperty(value = "问题状态（1待处理、2已处理）")
    private Integer status;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
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
