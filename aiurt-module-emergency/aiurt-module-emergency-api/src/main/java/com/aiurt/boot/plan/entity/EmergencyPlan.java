package com.aiurt.boot.plan.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @Description: emergency_plan
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_plan")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_plan对象", description="emergency_plan")
public class EmergencyPlan extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**应急预案类型*/
	@Excel(name = "应急预案类型", width = 15)
    @ApiModelProperty(value = "应急预案类型")
    @NotBlank(message = "应急预案类型不能为空")
    @Dict(dicCode = "emergency_plan_type")
    private Integer emergencyPlanType;
	/**应急预案名称*/
	@Excel(name = "应急预案名称", width = 15)
    @ApiModelProperty(value = "应急预案名称")
    @NotBlank(message = "应急预案名称不能为空")
    private String emergencyPlanName;
	/**应急预案版本*/
	@Excel(name = "应急预案版本", width = 15)
    @ApiModelProperty(value = "应急预案版本")
    private String emergencyPlanVersion;
	/**应急预案内容*/
	@Excel(name = "应急预案内容", width = 15)
    @ApiModelProperty(value = "应急预案内容")
    private String emergencyPlanContent;
	/**应急预案关键词*/
	@Excel(name = "应急预案关键词", width = 15)
    @ApiModelProperty(value = "应急预案关键词")
    private String keyWord;
	/**编制部门*/
	@Excel(name = "编制部门", width = 15)
    @ApiModelProperty(value = "编制部门")
    @Dict(dictTable = "sys_depart", dicCode = "org_code", dicText ="depart_name")
    private String orgCode;
	/**应急预案状态（1未启用、2启用中）*/
	@Excel(name = "启用状态（1已停用、2有效）", width = 15)
    @ApiModelProperty(value = "启用状态（1已停用、2有效）")
    @Dict(dicCode = "emergency_status")
    private Integer status;
	/**状态（1待提交、2待审核、3审核中、4已驳回、5已通过）*/
	@Excel(name = "流程状态（1待提审、2待审核、3审核中、4已驳回、5已通过）", width = 15)
    @ApiModelProperty(value = "流程状态（1待提审、2待审核、3审核中、4已驳回、5已通过）")
    @Dict(dicCode = "emergency_plan_status")
    private Integer emergencyPlanStatus;

    /**应急预案版本*/
    @Excel(name = "上一个应急预案版本id", width = 15)
    @ApiModelProperty(value = "上一个应急预案版本id")
    private String oldPlanId;

    /**启动应急预案版本*/
    @Excel(name = "启动应急预案版本", width = 15)
    @ApiModelProperty(value = "启动应急预案版本")
    @TableField(exist = false)
    private String planVersion;

	/**评审日期*/
	@Excel(name = "评审日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "评审日期")
    private java.util.Date approvedTime;

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

    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id")
    @TableField(exist = false)
    private String processInstanceId;
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    @TableField(exist = false)
    private String taskId;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String taskName;
    /**
     * 模板key，流程标识
     */
    @ApiModelProperty(value = "模板key，流程标识")
    @TableField(exist = false)
    private String modelKey;
}
