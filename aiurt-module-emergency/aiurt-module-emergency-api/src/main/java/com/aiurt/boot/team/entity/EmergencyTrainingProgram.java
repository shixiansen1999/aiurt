package com.aiurt.boot.team.entity;

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
    private String id;
	/**训练计划编号*/
	@Excel(name = "训练计划编号", width = 15)
    @ApiModelProperty(value = "训练计划编号")
    private String trainingProgramCode;
	/**训练项目名称*/
	@Excel(name = "训练项目名称", width = 15)
    @ApiModelProperty(value = "训练项目名称")
    private String trainingProgramName;
	/**计划训练时间*/
	@Excel(name = "计划训练时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "计划训练时间")
    private java.util.Date trainingPlanTime;
	/**计划训练人数*/
	@Excel(name = "计划训练人数", width = 15)
    @ApiModelProperty(value = "计划训练人数")
    private Integer traineesNum;
	/**审核状态（1待提交、2待完成、3已完成）*/
	@Excel(name = "审核状态（1待下发、2待完成、3已完成）", width = 15)
    @ApiModelProperty(value = "审核状态（1待下发、2待完成、3已完成）")
    private Integer status;
	/**编制部门*/
	@Excel(name = "编制部门", width = 15)
    @ApiModelProperty(value = "编制部门")
    private String orgCode;
    @ApiModelProperty(value = "编制部门名称")
    @TableField(exist = false)
    private String orgName;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
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

    /**应急队伍名称*/
    @ApiModelProperty(value = "应急队伍名称")
    private String emergencyTeamName;
}
