package com.aiurt.boot.modules.patrol.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: TaskDetailVO
 * @author: Mr.zhao
 * @date: 2021/9/22 17:21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TaskDetailVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "巡检表名称")
	private Long taskId;

	@ApiModelProperty(value = "巡检表名称")
	private String title;

	@ApiModelProperty(value = "巡检表说明")
	private String note;

	@ApiModelProperty(value = "班组名称")
	private String organizationName;

	@ApiModelProperty(value = "巡检人")
	private String staffName;

	@ApiModelProperty(value = "站点名称")
	private String stationName;

	@ApiModelProperty(value = "提交时间")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date submitTime;

	@ApiModelProperty(value = "工单编号")
	private String code;

	@ApiModelProperty(value = "巡检项")
	private List<PatrolPoolContentTreeVO> list;

	@ApiModelProperty("签名url")
	private String signature;

	@ApiModelProperty(value = "附件url")
	private List<String> url;

	@ApiModelProperty("状态 0.初始化 1.可进行更改")
	private Integer flag;

	@ApiModelProperty("巡检状态: 0.未巡检 1.已巡检")
	private Integer taskStatus;

	@ApiModelProperty(value = "抽查信息")
	private String spotTest;

	@ApiModelProperty(value = "抽查人id")
	private String spotTestUser;

	@ApiModelProperty(value = "技术员抽查内容")
	private String spotTestTechnician;

	@ApiModelProperty(value = "抽查技术员Id")
	private String spotTestTechnicianId;
}
