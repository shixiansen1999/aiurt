package com.aiurt.boot.modules.patrol.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: PatrolTaskVO
 * @author: Mr.zhao
 * @date: 2021/9/17 19:17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "巡检列表显示", description = "巡检列表显示")
public class PatrolTaskVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键id")
	private Long id;

	@ApiModelProperty(value = "计划池id")
	private Long poolId;

	@ApiModelProperty(value = "任务编号")
	private String code;

	@ApiModelProperty(value = "巡检表名称")
	private String name;

	@ApiModelProperty("备注")
	private String note;

	@ApiModelProperty(value = "巡检系统")
	private String systemType;

	@ApiModelProperty(value = "巡检系统名称")
	private String systemTypeName;

	@ApiModelProperty(value = "班组id")
	private String organizationId;

	@ApiModelProperty(value = "班组名称")
	private String organizationName;

	@ApiModelProperty(value = "站点id")
	private String lineId;

	@ApiModelProperty(value = "站点名称")
	private String lineName;

	@ApiModelProperty(value = "巡检人ids")
	private  String staffIds;

	@ApiModelProperty(value = "巡检人名称")
	private String staffName;

	@ApiModelProperty(value = "巡检频率: 1.一天1次 2.一周2次 3.一周1次")
	private String tactics;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "要求完成时间")
	private Date executionTime;

	@ApiModelProperty(value = "漏检任务处理状态: 0.否 1.是")
	private Integer ignoreStatus;

	@ApiModelProperty(value = "漏检任务处理信息")
	private String ignoreContent;

	@ApiModelProperty(value = "漏检任务时间")
	private String ignoreTime;

	@ApiModelProperty(value = "抽查信息")
	private String spotTest;

	@ApiModelProperty(value = "抽查人id")
	private String spotTestUser;

	@ApiModelProperty(value = "技术员抽查内容")
	private String spotTestTechnician;

	@ApiModelProperty(value = "抽查技术员Id")
	private String spotTestTechnicianId;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "提交时间")
	private Date submitTime;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "巡检要求完成时间")
	private Date createTime;

	@ApiModelProperty("指派状态 0.未指派 1.已指派")
	private Integer poolStatus;

	@ApiModelProperty("巡检状态 0.未巡检 1.已巡检")
	private Integer taskStatus;

	@ApiModelProperty("此条状态: 0.未指派(可指派/可领取) 1.可领取 2.巡检中 3.已完成 4.漏检")
	private Integer patrolFlag;

	@ApiModelProperty(value = "异常状态")
	private Integer warningStatus;



}
