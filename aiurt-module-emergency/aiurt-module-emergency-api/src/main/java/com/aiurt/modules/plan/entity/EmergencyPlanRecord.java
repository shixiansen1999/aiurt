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
 * @Description: emergency_plan_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_plan_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_plan_record对象", description="emergency_plan_record")
public class EmergencyPlanRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**事件分类*/
	@Excel(name = "事件分类", width = 15)
    @ApiModelProperty(value = "事件分类")
    private java.lang.Integer eventClass;
	/**事件性质*/
	@Excel(name = "事件性质", width = 15)
    @ApiModelProperty(value = "事件性质")
    private java.lang.Integer eventProperty;
	/**应急预案id*/
	@Excel(name = "应急预案id", width = 15)
    @ApiModelProperty(value = "应急预案id")
    private java.lang.String emergencyPlanId;
	/**应急预案版本*/
	@Excel(name = "应急预案版本", width = 15)
    @ApiModelProperty(value = "应急预案版本")
    private java.lang.String emergencyPlanVersion;
	/**启动日期*/
	@Excel(name = "启动日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "启动日期")
    private java.util.Date starttime;
	/**对完成预案及其他应急管理工作建议*/
	@Excel(name = "对完成预案及其他应急管理工作建议", width = 15)
    @ApiModelProperty(value = "对完成预案及其他应急管理工作建议")
    private java.lang.String advice;
	/**记录人*/
	@Excel(name = "记录人", width = 15)
    @ApiModelProperty(value = "记录人")
    private java.lang.String recorderId;
	/**记录时间*/
	@Excel(name = "记录时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "记录时间")
    private java.util.Date recordTime;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
}
