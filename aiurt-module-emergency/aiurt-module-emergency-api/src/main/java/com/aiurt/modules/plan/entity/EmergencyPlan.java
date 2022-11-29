package com.aiurt.modules.plan.entity;

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
public class EmergencyPlan implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
	/**应急预案类型*/
	@Excel(name = "应急预案类型", width = 15)
    @ApiModelProperty(value = "应急预案类型")
    private java.lang.Integer emergencyPlanType;
	/**应急预案名称*/
	@Excel(name = "应急预案名称", width = 15)
    @ApiModelProperty(value = "应急预案名称")
    private java.lang.String emergencyPlanName;
	/**应急预案版本*/
	@Excel(name = "应急预案版本", width = 15)
    @ApiModelProperty(value = "应急预案版本")
    private java.lang.String emergencyPlanVersion;
	/**应急预案内容*/
	@Excel(name = "应急预案内容", width = 15)
    @ApiModelProperty(value = "应急预案内容")
    private java.lang.String emergencyPlanContent;
	/**应急预案关键词*/
	@Excel(name = "应急预案关键词", width = 15)
    @ApiModelProperty(value = "应急预案关键词")
    private java.lang.String keyWord;
	/**编制部门*/
	@Excel(name = "编制部门", width = 15)
    @ApiModelProperty(value = "编制部门")
    private java.lang.String orgCode;
	/**应急预案状态（1未启用、2启用中）*/
	@Excel(name = "应急预案状态（1未启用、2启用中）", width = 15)
    @ApiModelProperty(value = "应急预案状态（1未启用、2启用中）")
    private java.lang.Integer status;
	/**状态（1待提交、2待审核、3审核中、4待发布、5已发布）*/
	@Excel(name = "状态（1待提交、2待审核、3审核中、4待发布、5已发布）", width = 15)
    @ApiModelProperty(value = "状态（1待提交、2待审核、3审核中、4待发布、5已发布）")
    private java.lang.Integer emergencyPlanStatus;
	/**发布人*/
	@Excel(name = "发布人", width = 15)
    @ApiModelProperty(value = "发布人")
    private java.lang.String publisher;
	/**发布时间*/
	@Excel(name = "发布时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "发布时间")
    private java.util.Date publishTime;
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
