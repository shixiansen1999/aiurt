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
 * @Description: emergency_plan_record_disposal_procedure
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_plan_record_disposal_procedure")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_plan_record_disposal_procedure对象", description="emergency_plan_record_disposal_procedure")
public class EmergencyPlanRecordDisposalProcedure implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**应急预案启动记录id*/
	@Excel(name = "应急预案启动记录id", width = 15)
    @ApiModelProperty(value = "应急预案启动记录id")
    private String emergencyPlanRecordId;
	/**处置部门*/
	@Excel(name = "处置部门", width = 15)
    @ApiModelProperty(value = "处置部门")
    private String orgCode;

    @ApiModelProperty(value = "处置部门名称")
    @TableField(exist = false)
    private String orgName;

	/**处置岗位（角色）*/
	@Excel(name = "处置岗位（角色）", width = 15)
    @ApiModelProperty(value = "处置岗位（角色）")
    private String roleId;

    @ApiModelProperty(value = "处置岗位（角色）名称")
    @TableField(exist = false)
    private String roleName;

	/**应急处置内容*/
	@Excel(name = "应急处置内容", width = 15)
    @ApiModelProperty(value = "应急处置内容")
    private String disposalProcedureContent;
	/**应急处置情况*/
	@Excel(name = "应急处置情况", width = 15)
    @ApiModelProperty(value = "应急处置情况")
    private String disposalProcedureSituation;
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
